package com.umc.pyeongsaeng.domain.job.converter;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.dto.request.JobPostRequestDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.FormFieldResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostImageResponseDTO;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.domain.job.search.document.JobPostDocument;
import com.umc.pyeongsaeng.global.client.google.GoogleGeocodingResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class JobPostConverter {


	public static JobPost toJobPost(JobPostRequestDTO.CreateDTO requestDTO, Company requestCompany, GoogleGeocodingResult convertedAddress) {

		return JobPost.builder()
			.title(requestDTO.getTitle())
			.address(requestDTO.getAddress())
			.detailAddress(requestDTO.getDetailAddress())
			.zipcode(requestDTO.getZipcode())
			.roadAddress(requestDTO.getRoadAddress())
			.description(requestDTO.getDescription())
			.hourlyWage(requestDTO.getHourlyWage())
			.monthlySalary(requestDTO.getMonthlySalary())
			.yearSalary(requestDTO.getYearSalary())
			.workingTime(requestDTO.getWorkingTime())
			.deadline(requestDTO.getDeadline())
			.recruitCount(requestDTO.getRecruitCount())
			.state(JobPostState.RECRUITING)
			.company(requestCompany)
			.images(new ArrayList<>())
			.formFields(new ArrayList<>())
			.note(requestDTO.getNote())
			.latitude(convertedAddress.lat())
			.longitude(convertedAddress.lon())
			.build();
	}

	public static JobPostResponseDTO.JobPostPreviewDTO toJobPostPreviewDTO(JobPost jobPost) {

		List<JobPostImageResponseDTO.JobPostImagePreviewDTO> jobPostImagePreviewDTOList = jobPost.getImages().stream()
			.map(JobPostImageConverter::toJobPostImagePreViewDTO).collect(Collectors.toList());

		List<FormFieldResponseDTO.FormFieldPreViewDTO> jobPostFormFieldPreviewDTOList = jobPost.getFormFields().stream()
			.map(FormFieldConverter::toFormFieldPreViewDTO).collect(Collectors.toList());

		return JobPostResponseDTO.JobPostPreviewDTO.builder()
			.id(jobPost.getId())
			.state(jobPost.getState())
			.title(jobPost.getTitle())
			.address(jobPost.getAddress())
			.detailAddress(jobPost.getDetailAddress())
			.zipcode(jobPost.getZipcode())
			.roadAddress(jobPost.getRoadAddress())
			.description(jobPost.getDescription())
			.hourlyWage(jobPost.getHourlyWage())
			.monthlySalary(jobPost.getMonthlySalary())
			.yearSalary(jobPost.getYearSalary())
			.workingTime(jobPost.getWorkingTime())
			.deadline(jobPost.getDeadline())
			.recruitCount(jobPost.getRecruitCount())
			.jobPostImageList(jobPostImagePreviewDTOList)
			.formFieldList(jobPostFormFieldPreviewDTOList)
			.note(jobPost.getNote())
			.build();
	}


	public static JobPostDocument toDocument(JobPost jobPost, GoogleGeocodingResult convertedAddress){

		// 대표 이미지
		String previewKeyname = jobPost.getImages().stream()
			.findFirst()
			.map(JobPostImage::getKeyName)
			.orElse(null);

		return JobPostDocument.builder()
			.id(String.valueOf(jobPost.getId()))
			.title(jobPost.getTitle())
			.description(jobPost.getDescription())
			.note(jobPost.getNote())
			.address(jobPost.getAddress())
			.sido(convertedAddress.sido())
			.sigungu(convertedAddress.sigungu())
			.bname(convertedAddress.bname())
			//.loc_cd(converted.locCode())
			.deadline(jobPost.getDeadline())
			.createdAt(jobPost.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant())
			.geoPoint(convertedAddress.lat()+","+convertedAddress.lon())
			.applicationCount(jobPost.getApplications() != null ? jobPost.getApplications().size() : 0)
			.keyname(previewKeyname)
			.build();
	}

	public static JobPostResponseDTO.JobPostPreviewByCompanyDTO toJobPostPreviewByCompanyDTO(JobPost jobPost,
																							 List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> images) {
		return JobPostResponseDTO.JobPostPreviewByCompanyDTO.builder()
			.id(jobPost.getId())
			.state(jobPost.getState())
			.title(jobPost.getTitle())
			.description(jobPost.getDescription())
			.roadAddress(jobPost.getRoadAddress())
			.images(images)
			.build();
	}


	public static JobPostResponseDTO.JobPostPreviewByCompanyListDTO toJobPostPreviewByCompanyListDTO(Page<JobPostResponseDTO.JobPostPreviewByCompanyDTO> jobPostList) {

		return JobPostResponseDTO.JobPostPreviewByCompanyListDTO.builder()
			.jobPostList(jobPostList.getContent())
			.isFirst(jobPostList.isFirst())
			.isLast(jobPostList.isLast())
			.totalElements(jobPostList.getTotalElements())
			.totalPage(jobPostList.getTotalPages())
			.listSize(jobPostList.getContent().size())
			.build();
	}

	public static JobPostResponseDTO.JobPostDetailDTO toJobPostDetailDTO(JobPost jobPost, String travelTime,
		List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> images) {
		return JobPostResponseDTO.JobPostDetailDTO.builder()
			.title(jobPost.getTitle())
			.address(jobPost.getAddress())
			.detailAddress(jobPost.getDetailAddress())
			.roadAddress(jobPost.getRoadAddress())
			.zipcode(jobPost.getZipcode())
			.hourlyWage(jobPost.getHourlyWage())
			.monthlySalary(jobPost.getMonthlySalary())
			.yearSalary(jobPost.getYearSalary())
			.description(jobPost.getDescription())
			.workingTime(jobPost.getWorkingTime())
			.deadline(jobPost.getDeadline())
			.recruitCount(jobPost.getRecruitCount())
			.note(jobPost.getNote())
			.images(images)
			.travelTime(travelTime)
			.build();
	}

	public static JobPostResponseDTO.JobPostTrendingDTO toJobPostTrendingDTO(JobPost jobPost,
																							 List<JobPostImageResponseDTO.JobPostImagePreviewWithUrlDTO> images) {
		return JobPostResponseDTO.JobPostTrendingDTO.builder()
			.id(jobPost.getId())
			.title(jobPost.getTitle())
			.description(jobPost.getDescription())
			.address(jobPost.getAddress())
			.images(images)
			.build();
	}

	public static JobPostResponseDTO.JobPostTrendingListDTO toJobPostTrendingLsitDTO(Page<JobPostResponseDTO.JobPostTrendingDTO> jobPostList) {

		return JobPostResponseDTO.JobPostTrendingListDTO.builder()
			.jobPostList(jobPostList.getContent())
			.isFirst(jobPostList.isFirst())
			.isLast(jobPostList.isLast())
			.totalElements(jobPostList.getTotalElements())
			.totalPage(jobPostList.getTotalPages())
			.listSize(jobPostList.getContent().size())
			.build();
	}

}
