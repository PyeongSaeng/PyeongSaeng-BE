package com.umc.pyeongsaeng.domain.user.dto;

import com.umc.pyeongsaeng.domain.senior.entity.*;
import com.umc.pyeongsaeng.domain.senior.enums.*;
import com.umc.pyeongsaeng.domain.user.entity.*;

import lombok.*;

public class UserResponse {
	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ProtectorInfoDto {
		private Long id;
		private String username;
		private String name;
		private String phone;

		public static ProtectorInfoDto from(User user) {
			return ProtectorInfoDto.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.phone(user.getPhone())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SeniorInfoDto {
		private Long id;
		private String username;
		private String name;
		private String phone;
		private Integer age;
		private String roadAddress;
		private String detailAddress;
		private JobType job;
		private ExperiencePeriod experiencePeriod;
		private Gender gender;

		public static SeniorInfoDto of(User user, SeniorProfile profile) {
			return SeniorInfoDto.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(user.getName())
				.phone(user.getPhone())
				.age(profile.getAge())
				.roadAddress(profile.getRoadAddress())
				.detailAddress(profile.getDetailAddress())
				.job(profile.getJob())
				.experiencePeriod(profile.getExperiencePeriod())
				.gender(profile.getGender())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ConnectedSeniorDto {
		private Long seniorId;
		private String seniorName;
		private Gender gender;
		private Integer age;
		private String seniorPhone;
		private String roadAddress;
		private String detailAddress;

		public static ConnectedSeniorDto of(User senior, SeniorProfile profile) {
			return ConnectedSeniorDto.builder()
				.seniorId(senior.getId())
				.seniorName(senior.getName())
				.gender(profile.getGender())
				.age(profile.getAge())
				.seniorPhone(profile.getPhoneNum())
				.roadAddress(profile.getRoadAddress())
				.detailAddress(profile.getDetailAddress())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UsernameDto {
		private String username;

		public static UsernameDto from(User user) {
			return UsernameDto.builder()
				.username(user.getUsername())
				.build();
		}
	}

	@Getter
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SeniorSearchResultDto {
		private Long id;
		private String name;
		private String phone;
		private boolean isAlreadyConnected;

		public static SeniorSearchResultDto of(User senior, boolean isConnected) {
			return SeniorSearchResultDto.builder()
				.id(senior.getId())
				.name(senior.getName())
				.phone(senior.getPhone())
				.isAlreadyConnected(isConnected)
				.build();
		}
	}
}
