package com.umc.pyeongsaeng.domain.application.service;


import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

public interface ApplicationCommandService  {

	/**
	 * 특정 지원서의 상태를 변경합니다. (예: 승인, 거절)
	 *
	 * @param applicationId 상태를 변경할 지원서의 ID
	 * @param requestDTO 변경할 상태 정보를 담은 DTO
	 * @return 상태가 변경된 Application 엔티티
	 */
	Application updateApplicationState(Long applicationId, ApplicationRequestDTO.ApplicationStatusRequestDTO requestDTO);

	/**
	 * 대리자가 제출한 지원서를 생성합니다.
	 *
	 * @param requestDTO 지원서 생성에 필요한 요청 데이터 (공고 ID, 답변 등)
	 * @param applicant 지원서를 제출하는 사용자(시니어 또는 본인)
	 * @return 생성된 지원서의 주요 정보를 담은 DTO
	 */
	ApplicationResponseDTO.RegistrationResultDTO createDelegateApplication(ApplicationRequestDTO.DelegateRegistrationRequestDTO requestDTO, User applicant);

	/**
	 * 사용자가 직접 제출한 지원서를 생성합니다.
	 *
	 * @param requestDTO 지원서 생성에 필요한 요청 데이터 (공고 ID, 답변 등)
	 * @param applicant 지원서를 제출하는 사용자(시니어 또는 본인)
	 * @return 생성된 지원서의 주요 정보를 담은 DTO
	 */
	ApplicationResponseDTO.RegistrationResultDTO createDirectApplication(ApplicationRequestDTO.DirectRegistrationRequestDTO requestDTO, User applicant);


	ApplicationResponseDTO.RegistrationResultDTO updateTmpApplicationToFinalApplication(ApplicationRequestDTO.TmpToFinalRegistrationRequestDTO requestDTO, Long applicationId, User applicant);

	/**
	 * 특정 채용공고에 대한 "작성 전" 상태의 지원서를 신청함에 저장합니다.
	 *
	 * 시니어-채용공고 조합에 이미 지원서가 존재하면 새로 만들지 않고 기존 엔티티를 반환합니다
	 * 존재하는 경우 클릭한 채용공고가 목록에서 상단에 보이도록 updatedAt을 갱신합니다.
	 * 최초 생성 시 상태는 NON_STARTED 입니다.
	 *
	 * @param jobPostId   신청할 채용공고 ID
	 * @param userDetails 인증 사용자 정보(시니어)
	 * @return 생성되었거나(또는 존재하던) Application 엔티티
	 */
	Application createIfNotExists(Long jobPostId, CustomUserDetails userDetails);

	/**
	 * 본인(시니어)가 특정 지원서를 삭제합니다.
	 *
	 * @param applicationId 삭제할 지원서 ID
	 * @param seniorId 시니어 ID
	 */
	void deleteApplication(Long applicationId, Long seniorId);

}
