package com.umc.pyeongsaeng.domain.application.service;

import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.application.entity.Application;

public interface ApplicationQueryService {

	Page<Application> findCompanyApplications(Long jobPostId,Integer page);
}
