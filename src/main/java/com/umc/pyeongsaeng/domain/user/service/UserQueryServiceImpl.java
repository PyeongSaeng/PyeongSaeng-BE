package com.umc.pyeongsaeng.domain.user.service;

import java.util.*;
import java.util.stream.*;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.repository.*;
import com.umc.pyeongsaeng.domain.sms.service.*;
import com.umc.pyeongsaeng.domain.user.dto.*;
import com.umc.pyeongsaeng.domain.user.entity.*;
import com.umc.pyeongsaeng.domain.user.enums.*;
import com.umc.pyeongsaeng.domain.user.repository.*;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.*;
import com.umc.pyeongsaeng.global.apiPayload.code.status.*;

import lombok.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

	private final UserRepository userRepository;
	private final SeniorProfileRepository seniorProfileRepository;
	private final SocialAccountRepository socialAccountRepository;
	private final SmsService smsService;

	// 보호자 정보 조회
	@Override
	public UserResponse.ProtectorInfoDto getProtectorInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		return UserResponse.ProtectorInfoDto.from(user);
	}

	// 시니어 정보 조회
	@Override
	public UserResponse.SeniorInfoDto getSeniorInfo(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (user.getRole() != Role.SENIOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		SeniorProfile seniorProfile = seniorProfileRepository.findById(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));

		return UserResponse.SeniorInfoDto.of(user, seniorProfile);
	}

	// 특정 보호자와 연결된 시니어 목록 조회
	@Override
	public List<UserResponse.ConnectedSeniorDto> getConnectedSeniors(Long protectorId) {
		User protector = userRepository.findById(protectorId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (protector.getRole() != Role.PROTECTOR) {
			throw new GeneralException(ErrorStatus.INVALID_USER_ROLE);
		}

		List<SeniorProfile> seniorProfiles = seniorProfileRepository.findByProtectorId(protectorId);

		return seniorProfiles.stream()
			.map(profile -> UserResponse.ConnectedSeniorDto.of(profile.getSenior(), profile))
			.collect(Collectors.toList());
	}

	// 아이디 찾기
	@Override
	public UserResponse.UsernameDto findUsername(UserRequest.FindUsernameDto request) {
		smsService.verifyCode(request.getPhone(), request.getVerificationCode());

		User user = userRepository.findByNameAndPhone(request.getName(), request.getPhone())
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		return UserResponse.UsernameDto.from(user);
	}

	// 전화번호로 시니어 검색
	@Override
	public UserResponse.SeniorSearchResultDto searchSeniorByPhone(String phone, Long protectorId) {
		User senior = userRepository.findByPhone(phone)
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		if (senior.getRole() != Role.SENIOR) {
			throw new GeneralException(ErrorStatus.NOT_SENIOR_ROLE);
		}

		if (senior.getStatus() != UserStatus.ACTIVE) {
			throw new GeneralException(ErrorStatus.ALREADY_WITHDRAWN);
		}

		boolean isAlreadyConnected = false;
		if (protectorId != null) {
			isAlreadyConnected = seniorProfileRepository
				.existsBySeniorIdAndProtectorId(senior.getId(), protectorId);
		}

		return UserResponse.SeniorSearchResultDto.of(senior, isAlreadyConnected);
	}
}
