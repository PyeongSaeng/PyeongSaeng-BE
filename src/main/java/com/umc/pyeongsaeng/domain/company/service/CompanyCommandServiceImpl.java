package com.umc.pyeongsaeng.domain.company.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.company.dto.CompanyRequest;
import com.umc.pyeongsaeng.domain.company.dto.CompanyResponse;
import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.enums.CompanyStatus;
import com.umc.pyeongsaeng.domain.company.repository.CompanyRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.sms.service.SmsService;
import com.umc.pyeongsaeng.domain.token.repository.RefreshTokenRepository;
import com.umc.pyeongsaeng.domain.token.service.TokenService;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import com.umc.pyeongsaeng.global.client.nts.NtsApiClient;
import com.umc.pyeongsaeng.global.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyCommandServiceImpl implements CompanyCommandService {
	private static final int WITHDRAWAL_GRACE_DAYS = 7;

	private final TokenService tokenService;
	private final CompanyRepository companyRepository;
	private final PasswordEncoder passwordEncoder;
	private final NtsApiClient ntsApiClient;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JobPostRepository jobPostRepository;
	private final JwtUtil jwtUtil;
	private final SmsService smsService;

	// 회원가입
	@Override
	public CompanyResponse.CompanySignUpResponseDto signUp(CompanyRequest.CompanySignUpRequestDto request) {
		smsService.verifyCode(request.getPhone(), request.getVerificationCode());

		validateDuplicateUsername(request.getUsername());
		validateDuplicateBusinessNo(request.getBusinessNo());
		validateDuplicatePhone(request.getPhone());

		validateBusinessNumber(request.getBusinessNo());

		Company company = createCompany(request);
		Company savedCompany = companyRepository.save(company);

		return CompanyResponse.CompanySignUpResponseDto.builder()
			.companyId(savedCompany.getId())
			.username(savedCompany.getUsername())
			.businessNo(savedCompany.getBusinessNo())
			.companyName(savedCompany.getCompanyName())
			.ownerName(savedCompany.getOwnerName())
			.phone(savedCompany.getPhone())
			.build();
	}

	// 로그인
	@Override
	public CompanyResponse.LoginResponseDto login(CompanyRequest.LoginRequestDto request) {
		Company company = companyRepository.findByUsername(request.getUsername())
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		if (company.getStatus() == CompanyStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.WITHDRAWN_COMPANY);
		}

		if (!passwordEncoder.matches(request.getPassword(), company.getPassword())) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}

		String accessToken = jwtUtil.generateAccessToken(company.getId(), "COMPANY");
		String refreshToken = jwtUtil.generateRefreshToken(company.getId(), "COMPANY");

		tokenService.saveCompanyRefreshToken(company.getId(), refreshToken);

		String refreshTokenCookie = tokenService.createRefreshTokenCookie(refreshToken).toString();

		return CompanyResponse.LoginResponseDto.builder()
			.companyId(company.getId())
			.username(company.getUsername())
			.businessNo(company.getBusinessNo())
			.accessToken(accessToken)
			.refreshTokenCookie(refreshTokenCookie)
			.build();
	}

	// 로그아웃
	@Override
	public void logout(Long companyId) {
		refreshTokenRepository.deleteByCompany_Id(companyId);
	}

	// 로그아웃 시 리프레시 토큰 쿠키 제거 명령 생성
	@Override
	public String getLogoutCookie() {
		return tokenService.deleteRefreshTokenCookie().toString();
	}

	// 프로필 수정
	@Override
	public CompanyResponse.CompanyInfoDto updateProfile(Long companyId, CompanyRequest.UpdateProfileRequestDto request) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		company.updateProfile(request.getCompanyName(), request.getOwnerName(), request.getPhone());

		if (request.isPasswordChangeRequested()) {
			validateCurrentPassword(company, request.getCurrentPassword());
			company.changePassword(passwordEncoder.encode(request.getNewPassword()));
		}

		return CompanyResponse.CompanyInfoDto.builder()
			.companyId(company.getId())
			.username(company.getUsername())
			.businessNo(company.getBusinessNo())
			.phone(company.getPhone())
			.companyName(company.getCompanyName())
			.ownerName(company.getOwnerName())
			.build();
	}

	// confirmed로 의도 확인 후 UserStatus WITHDRAWN으로 변경
	@Override
	public void withdrawCompany(Long companyId, boolean confirmed) {
		Company company = companyRepository.findById(companyId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		if (company.getStatus() == CompanyStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN_COMPANY);
		}

		validateWithdrawalIntent(confirmed);

		company.withdraw();
		tokenService.deleteRefreshToken(companyId);
	}

	// UserStatus를 Active로 변경
	@Override
	public void cancelWithdrawal(String username) {
		Company company = companyRepository.findByUsername(username)
			.orElseThrow(() -> new GeneralException(ErrorStatus.COMPANY_NOT_FOUND));

		if (company.getStatus() != CompanyStatus.WITHDRAWN) {
			throw new GeneralException(ErrorStatus.NOT_WITHDRAWN_COMPANY);
		}

		if (company.getWithdrawnAt() != null &&
			company.getWithdrawnAt().plusDays(WITHDRAWAL_GRACE_DAYS).isBefore(LocalDateTime.now())) {
			throw new GeneralException(ErrorStatus.WITHDRAWAL_PERIOD_EXPIRED);
		}

		company.cancelWithdrawal();
	}

	// 연관 데이터, 사용자 데이터 모두 삭제
	@Scheduled(cron = "0 0 3 * * *")
	public void deleteExpiredWithdrawnCompanies() {
		LocalDateTime expiryDate = LocalDateTime.now().minusDays(WITHDRAWAL_GRACE_DAYS);

		List<Company> expiredCompanies = companyRepository.findByStatusAndWithdrawnAtBefore(
			CompanyStatus.WITHDRAWN, expiryDate);

		if (!expiredCompanies.isEmpty()) {
			for (Company company : expiredCompanies) {
				deleteAllRelatedData(company.getId());
				companyRepository.delete(company);
			}
		}
	}

	// 전화번호 중복 여부 확인
	private void validateDuplicatePhone(String phone) {
		if (companyRepository.existsByPhone(phone)) {
			throw new GeneralException(ErrorStatus.DUPLICATE_PHONE);
		}
	}

	// id 중복 여부 확인
	private void validateDuplicateUsername(String username) {
		if (companyRepository.existsByUsername(username)) {
			throw new GeneralException(ErrorStatus.DUPLICATE_USERNAME);
		}
	}

	// 사업자번호 중복 여부 확인
	private void validateDuplicateBusinessNo(String businessNo) {
		if (companyRepository.existsByBusinessNo(businessNo)) {
			throw new GeneralException(ErrorStatus.DUPLICATE_BUSINESS_NO);
		}
	}

	// 외부 API(국세청)를 통해 사업자번호가 활성화 상태인지 검증
	private void validateBusinessNumber(String businessNo) {
		if (!ntsApiClient.isActiveBusinessNumber(businessNo)) {
			throw new GeneralException(ErrorStatus.INVALID_BUSINESS_NO);
		}
	}

	// company 객체 생성
	private Company createCompany(CompanyRequest.CompanySignUpRequestDto request) {
		return Company.builder()
			.ownerName(request.getOwnerName())
			.phone(request.getPhone())
			.companyName(request.getCompanyName())
			.businessNo(request.getBusinessNo())
			.username(request.getUsername())
			.password(passwordEncoder.encode(request.getPassword()))
			.status(CompanyStatus.ACTIVE)
			.build();
	}

	// 현재 비밀번호 검증
	private void validateCurrentPassword(Company company, String currentPassword) {
		if (!passwordEncoder.matches(currentPassword, company.getPassword())) {
			throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
		}
	}

	// 탈퇴 의도 확인
	private void validateWithdrawalIntent(boolean confirmed) {
		if (!confirmed) {
			throw new GeneralException(ErrorStatus.COMPANY_WITHDRAWAL_NOT_CONFIRMED);
		}
	}

	// 기업 관련 모든 데이터 삭제
	private void deleteAllRelatedData(Long companyId) {
		jobPostRepository.deleteByCompanyId(companyId);
		refreshTokenRepository.deleteByCompany_Id(companyId);
	}
}
