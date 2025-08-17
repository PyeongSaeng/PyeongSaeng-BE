package com.umc.pyeongsaeng.domain.application.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswer;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepositoryCustom;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.entity.JobPostImage;
import com.umc.pyeongsaeng.domain.job.enums.FieldType;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationConverter {

	private final ObjectMapper objectMapper;
	public static ApplicationResponseDTO.ApplicationPreViewDTO toApplicationPreViewDTO(Application application) {

		return ApplicationResponseDTO.ApplicationPreViewDTO.builder()
			.applicationId(application.getId())
			.applicantName(application.getApplicant().getName())
			.applicationStatus(application.getApplicationStatus())
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

	public static ApplicationResponseDTO.ApplicationStateResponseDTO toApplicationStateResponseDTO(Application application) {

		return ApplicationResponseDTO.ApplicationStateResponseDTO.builder()
			.applicationId(application.getId())
			.applicationStatus(application.getApplicationStatus())
			.build();
	}

	public ApplicationResponseDTO.ApplicationQnADetailPreViewDTO toApplicationQnADetailPreViewDTO(
		ApplicationRepositoryCustom.ApplicationDetailView result
	) {
		List<ApplicationResponseDTO.ApplicationQnADTO> questionAndAnswerList;

		try {
			String jsonQnAString = result.getQuestionAndAnswer();
			if (jsonQnAString == null || jsonQnAString.isEmpty() || "null".equalsIgnoreCase(jsonQnAString)) {
				questionAndAnswerList = new ArrayList<>();
			} else {
				// QnA 전체 JSON 문자열을 DTO 리스트로 파싱
				// 예시 값 [{"fieldName": "이름", "fieldType": "Text", "answerContent": "김시니어"}, {"fieldName": "경력사항", "fieldType": "IMAGE", "answerContent": "[{\"keyName\": \"resume_2025.pdf\", \"originalFileName\": \"이력서.pdf\"}]"}]
				// 결과로 ApplicationQnADTO에 맞는 객체 생성
				questionAndAnswerList = objectMapper.readValue(
					jsonQnAString,
					new TypeReference<List<ApplicationResponseDTO.ApplicationQnADTO>>() {}
				);

				// 리스트를 순회하며 이미지 타입의 답변을 추가로 처리
				for (ApplicationResponseDTO.ApplicationQnADTO qna : questionAndAnswerList) {
					// fieldType이 'IMAGE'이고, answerContent가 아직 문자열일 경우
					if ("IMAGE".equals(qna.getFieldType()) && qna.getAnswerContent() instanceof String) {
						String imageJsonString = (String) qna.getAnswerContent();
						// 중첩된 JSON 문자열을 List<ApplicationFilePreViewDTO>로 파싱
						List<ApplicationResponseDTO.ApplicationFilePreViewDTO> imageFiles = objectMapper.readValue(
							imageJsonString,
							new TypeReference<List<ApplicationResponseDTO.ApplicationFilePreViewDTO>>() {}
						);
						qna.setAnswerContent(imageFiles); // 기존 문자열을 파싱된 객체 리스트로 교체
					}
				}
			}
		} catch (JsonProcessingException e) {
			throw new GeneralException(ErrorStatus.APPLICATION_PARSING_ERROR);
		}

		return ApplicationResponseDTO.ApplicationQnADetailPreViewDTO.builder()
			.questionAndAnswerList(questionAndAnswerList)
			.postState(JobPostState.valueOf(result.getPost_state()))
			.applicationState(result.getApplication_status())
			.build();
	}

	public static ApplicationResponseDTO.RegistrationResultDTO toRegistrationResultDTO(Application application, List<ApplicationAnswer> answers) {

		List<ApplicationResponseDTO.AnswerResponseDTO> answerResultList = answers.stream()
			.map(answer -> {
				// 답변의 타입에 따라 적절한 ResponseDTO를 생성
				if (answer.getFormField().getFieldType() == FieldType.TEXT) {
					return new ApplicationResponseDTO.TextAnswerResponseDTO(answer);
				} else if (answer.getFormField().getFieldType() == FieldType.IMAGE) {
					return new ApplicationResponseDTO.ImageAnswerResponseDTO(answer);
				}
				return null;
			})
			.filter(Objects::nonNull)
			.collect(Collectors.toList());

		return ApplicationResponseDTO.RegistrationResultDTO.builder()
			.applicationId(application.getId())
			.jobPostId(application.getJobPost().getId())
			.applicationStatus(application.getApplicationStatus().name())
			.createdAt(application.getCreatedAt())
			.answers(answerResultList)
			.build();
	}

	public static ApplicationResponseDTO.ImagePreviewWithUrlDTO toImagePreviewWithUrlDTO(JobPostImage jobPostImage, String imageUrl) {

		return ApplicationResponseDTO.ImagePreviewWithUrlDTO.builder()
			.originalFileName(jobPostImage.getOriginalFileName())
			.keyName(jobPostImage.getKeyName())
			.imageId(jobPostImage.getId())
			.imageUrl(imageUrl)
			.build();
	}

	public static ApplicationResponseDTO.SubmittedApplicationResponseDTO toSubmittedApplicationResponseDTO(
		Application application, JobPost jobPost, List<ApplicationResponseDTO.ImagePreviewWithUrlDTO> images) {

		return ApplicationResponseDTO.SubmittedApplicationResponseDTO.builder()
			.applicationId(application.getId())
			.jobPostId(jobPost.getId())
			.deadline(jobPost.getDeadline())
			.title(jobPost.getTitle())
			.applicationStatus(application.getApplicationStatus())
			.images(images)
			.build();
	}

	public static ApplicationResponseDTO.SubmittedApplicationResponseListDTO toSubmittedApplicationResponseListDTO(
		Page<ApplicationResponseDTO.SubmittedApplicationResponseDTO> submittedApplicationResponseDTOList) {

		return ApplicationResponseDTO.SubmittedApplicationResponseListDTO.builder()
			.applicationList(submittedApplicationResponseDTOList.getContent())
			.isFirst(submittedApplicationResponseDTOList.isFirst())
			.isLast(submittedApplicationResponseDTOList.isLast())
			.listSize(submittedApplicationResponseDTOList.getContent().size())
			.totalElements(submittedApplicationResponseDTOList.getTotalElements())
			.totalPage(submittedApplicationResponseDTOList.getTotalPages())
			.build();
	}


	public ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO toSubmittedApplicationQnADetailResponseDTO(
		JobPost jobPost,
		ApplicationRepositoryCustom.ApplicationDetailView result,
		String travelTime,
		List<ApplicationResponseDTO.ImagePreviewWithUrlDTO> images
	) {
		List<ApplicationResponseDTO.ApplicationQnADTO> questionAndAnswerList;

		try {
			String jsonQnAString = result.getQuestionAndAnswer();
			if (jsonQnAString == null || jsonQnAString.isEmpty() || "null".equalsIgnoreCase(jsonQnAString)) {
				questionAndAnswerList = new ArrayList<>();
			} else {
				questionAndAnswerList = objectMapper.readValue(
					jsonQnAString,
					new TypeReference<List<ApplicationResponseDTO.ApplicationQnADTO>>() {}
				);

				for (ApplicationResponseDTO.ApplicationQnADTO qna : questionAndAnswerList) {
					if ("IMAGE".equals(qna.getFieldType()) && qna.getAnswerContent() instanceof String) {
						String imageJsonString = (String) qna.getAnswerContent();
						List<ApplicationResponseDTO.ApplicationFilePreViewDTO> imageFiles = objectMapper.readValue(
							imageJsonString,
							new TypeReference<List<ApplicationResponseDTO.ApplicationFilePreViewDTO>>() {}
						);
						qna.setAnswerContent(imageFiles); // 기존 문자열을 파싱된 객체 리스트로 교체
					}
				}
			}
		} catch (JsonProcessingException e) {
			throw new GeneralException(ErrorStatus.APPLICATION_PARSING_ERROR);
		}

		return ApplicationResponseDTO.SubmittedApplicationQnADetailResponseDTO.builder()
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
			.questionAndAnswerList(questionAndAnswerList)
			.build();
	}

	public ApplicationResponseDTO.ApplicationJobPostStatusDTO toJobPostStatusDTO(Application application) {
		return ApplicationResponseDTO.ApplicationJobPostStatusDTO.builder()
			.applicationId(application.getId())
			.jobPostId(application.getJobPost().getId())
			.applicationStatus(application.getApplicationStatus())
			.build();
	}

}
