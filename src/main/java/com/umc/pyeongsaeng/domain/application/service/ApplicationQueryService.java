package com.umc.pyeongsaeng.domain.application.service;

import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.user.entity.User;
import org.springframework.data.domain.Page;

public interface ApplicationQueryService {

	Page<Application> findCompanyApplications(Long jobPostId,Integer page);

	ApplicationResponseDTO.ApplicationQnADetailPreViewDTO getApplicationQnADetail(Long applicationId);

	Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> getSubmittedApplication(User seniorId, Integer page);

	ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO getSubmittedApplicationDetails(Long applicationId, Long userId);
}
