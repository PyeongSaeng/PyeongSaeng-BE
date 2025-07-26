package com.umc.pyeongsaeng.domain.application.converter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;

public class ApplicationConverter {

	public static ApplicationResponseDTO.ApplicationPreViewDTO toApplicationPreViewDTO(Application application) {

		return ApplicationResponseDTO.ApplicationPreViewDTO.builder()
			.applicationId(application.getId())
			.applicantName(application.getApplicant().getName())
			.build();
	}

	public static ApplicationResponseDTO.ApplicationPreViewListDTO toApplicationPreViewListDTO(Page<Application> applicationList) {

		List<ApplicationResponseDTO.ApplicationPreViewDTO> applicationPreViewDTOList = applicationList.stream()
			.map(ApplicationConverter::toApplicationPreViewDTO).collect(Collectors.toList());

		return ApplicationResponseDTO.ApplicationPreViewListDTO.builder()
			.applicationList(applicationPreViewDTOList)
			.totalPage(applicationList.getTotalPages())
			.totalElements(applicationList.getTotalElements())
			.listSize(applicationPreViewDTOList.size())
			.isFirst(applicationList.isFirst())
			.isLast(applicationList.isLast())
			.build();
	}
}
