package com.umc.pyeongsaeng.domain.application.service;


import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.user.entity.User;

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


}
