package com.umc.pyeongsaeng.domain.application.converter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepositoryCustom;
import com.umc.pyeongsaeng.domain.job.enums.JobPostState;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationConverter {

	private final ObjectMapper objectMapper;
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
}
