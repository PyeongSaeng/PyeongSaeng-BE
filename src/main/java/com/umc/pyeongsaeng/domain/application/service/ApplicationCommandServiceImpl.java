package com.umc.pyeongsaeng.domain.application.service;

import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationCommandServiceImpl implements ApplicationCommandService {

	private final ApplicationRepository applicationRepository;

	@Override
	public Application updateApplicationState(Long applicationId, ApplicationRequestDTO.ApplicationStatusRequestDTO requestDTO) {

		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		application.setApplicationStatus(ApplicationStatus.valueOf(requestDTO.getApplicationStatus()));
		return application;
	}
}
