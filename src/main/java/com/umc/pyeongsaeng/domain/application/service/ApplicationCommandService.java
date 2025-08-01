package com.umc.pyeongsaeng.domain.application.service;


import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;

public interface ApplicationCommandService  {

	Application updateApplicationState(Long applicationId, ApplicationRequestDTO.ApplicationStatusRequestDTO requestDTO);
}
