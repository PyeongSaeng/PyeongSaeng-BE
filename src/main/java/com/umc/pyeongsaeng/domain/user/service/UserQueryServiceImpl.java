package com.umc.pyeongsaeng.domain.user.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.user.dto.UserResponse;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.Role;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {

	private final UserRepository userRepository;
	private final SeniorProfileRepository seniorProfileRepository;

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
			.map(profile -> UserResponse.ConnectedSeniorDto.of(
				profile.getSenior(),
				profile.getPhoneNum(),
				profile.getRelation()
			))
			.collect(Collectors.toList());
	}
}
