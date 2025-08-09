package com.umc.pyeongsaeng.domain.application.service;

import java.util.List;

import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.user.entity.User;
import org.springframework.data.domain.Page;

public interface ApplicationQueryService {

	Page<Application> findCompanyApplications(Long jobPostId,Integer page);

	ApplicationResponseDTO.ApplicationQnADetailPreViewDTO getApplicationQnADetail(Long applicationId);

	Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> getSubmittedApplication(User seniorId, Integer page);

	ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO getSubmittedApplicationDetails(Long applicationId, Long userId);

	/**
	 * 특정 시니어(본인)의 신청함 목록을 조회합니다.
	 *
	 * 기본적으로 {@code NON_STARTED} (작성 전), {@code DRAFT} (임시저장) 상태의 신청서를 반환합니다.
	 * 정렬 기준은 {@code updatedAt} 기준으로 최신순 정렬됩니다.
	 * 각 항목은 채용공고 ID, 신청서 상태, 신청서 ID 등을 포함하며, 채용공고 상세는 별도 API로 조회합니다.
	 *
	 * @param seniorId 신청함을 조회할 시니어(본인) 사용자 ID
	 * @return 신청서 상태와 채용공고 ID를 포함한 DTO 리스트
	 */
	List<ApplicationResponseDTO.ApplicationJobPostStatusDTO> getApplicationsForSenior(Long seniorId);

	/**
	 * 보호자와 연결된 모든 시니어의 신청함 목록을 조회합니다.
	 *
	 * 보호자 ID를 기준으로 연결된 모든 시니어의 신청서를 모아 반환합니다.
	 * 각 항목에는 시니어 이름, 시니어 ID, 채용공고 ID 등 보호자가 확인할 수 있는 최소한의 정보가 포함됩니다.
	 * 상세 정보(채용공고 내용, 거리 계산 등)는 별도 상세 조회 API를 통해 제공됩니다.
	 *
	 * @param protectorId 신청함을 조회할 보호자 사용자 ID
	 * @return 연결된 시니어들의 신청서 목록 DTO 리스트
	 */
	List<ApplicationResponseDTO.ProtectorApplicationJobPostDTO> getProtectorApplications(Long protectorId);
}
