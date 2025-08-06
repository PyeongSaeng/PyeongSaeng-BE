package com.umc.pyeongsaeng.domain.application.service;

import com.umc.pyeongsaeng.domain.application.converter.ApplicationConverter;
import com.umc.pyeongsaeng.domain.application.dto.request.ApplicationRequestDTO;
import com.umc.pyeongsaeng.domain.application.dto.response.ApplicationResponseDTO;
import com.umc.pyeongsaeng.domain.application.entity.Application;
import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswer;
import com.umc.pyeongsaeng.domain.application.entity.ApplicationAnswerFile;
import com.umc.pyeongsaeng.domain.application.enums.ApplicationStatus;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationAnswerFileRepository;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationAnswerRepository;
import com.umc.pyeongsaeng.domain.application.repository.ApplicationRepository;
import com.umc.pyeongsaeng.domain.job.entity.FormField;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.FormFieldRepository;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.repository.UserRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationCommandServiceImpl implements ApplicationCommandService {

	private final ApplicationRepository applicationRepository;
	private final JobPostRepository jobPostRepository;
	private final UserRepository userRepository;
	private final FormFieldRepository formFieldRepository;
	private final ApplicationAnswerRepository applicationAnswerRepository;
	private final ApplicationAnswerFileRepository applicationAnswerFileRepository;

	@Override
	public Application updateApplicationState(Long applicationId, ApplicationRequestDTO.ApplicationStatusRequestDTO requestDTO) {

		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_APPLICATION_ID));

		application.setApplicationStatus(ApplicationStatus.valueOf(requestDTO.getApplicationStatus()));
		return application;
	}

	@Override
	public ApplicationResponseDTO.RegistrationResultDTO createApplication(ApplicationRequestDTO.RegistrationRequestDTO requestDTO, User applicant) {

		JobPost jobPost = jobPostRepository.findById(requestDTO.getJobPostId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));

		User senior = userRepository.findById(requestDTO.getSeniorId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

		// 지원서(Application) 생성 및 저장
		Application newApplication = Application.builder()
			.applicant(applicant)
			.applicationStatus(requestDTO.getApplicationStatus())
			.jobPost(jobPost)
			.senior(senior)
			.build();

		Application savedApplication = applicationRepository.save(newApplication);

		// DTO로 Return 해주기 위해서 저장된 Answeer
		List<ApplicationAnswer> savedApplicationAnswer = new ArrayList<>();

		// FormField 미리 조회
		List<Long> formFieldIds = requestDTO.getFieldAndAnswer().stream()
			.map(ApplicationRequestDTO.FieldAndAnswerDTO::getFormFieldId)
			.collect(Collectors.toList());

		// { formFieldId, formField }형식의 Map 생성
		Map<Long, FormField> formFieldMap = formFieldRepository.findAllById(formFieldIds).stream()
			.collect(Collectors.toMap(FormField::getId, Function.identity()));

		// answer의 타입별 분기 처리
		for (ApplicationRequestDTO.FieldAndAnswerDTO field : requestDTO.getFieldAndAnswer()) {
			FormField formField = formFieldMap.get(field.getFormFieldId());
			if (formField == null) {
				throw new GeneralException(ErrorStatus.FORM_FIELD_NOT_FOUND);
			}

			// answer의 현재 타입별 저장
			if (field instanceof ApplicationRequestDTO.TextFieldDTO) {
				savedApplicationAnswer.add(saveTextAnswer((ApplicationRequestDTO.TextFieldDTO) field, savedApplication, formField));
			} else if (field instanceof ApplicationRequestDTO.ImageFieldDTO) {
				savedApplicationAnswer.add(saveImageAnswer((ApplicationRequestDTO.ImageFieldDTO) field, savedApplication, formField));
			}

		}

		return ApplicationConverter.toRegistrationResultDTO(savedApplication, savedApplicationAnswer);

	}

	private ApplicationAnswer saveTextAnswer(ApplicationRequestDTO.TextFieldDTO textField, Application savedApplication, FormField formField) {
		ApplicationAnswer applicationAnswer = ApplicationAnswer.builder()
			.formField(formField)
			.application(savedApplication)
			.answerText(textField.getAnswer())
			.build();
		return applicationAnswerRepository.save(applicationAnswer);
	}

	private ApplicationAnswer saveImageAnswer(ApplicationRequestDTO.ImageFieldDTO imageField, Application savedApplication, FormField formField) {
		// 이미지 답변의 경우 answerText는 null
		ApplicationAnswer applicationAnswer = ApplicationAnswer.builder()
			.formField(formField)
			.application(savedApplication)
			.answerText(null)
			.build();
		ApplicationAnswer savedAnswer = applicationAnswerRepository.save(applicationAnswer);

		// 연관된 파일들 생성 및 저장
		List<ApplicationAnswerFile> filesToSave = imageField.getAnswer().stream()
			.map(imageInfo -> ApplicationAnswerFile.builder()
				.applicationAnswer(savedAnswer)
				.keyName(imageInfo.getKeyName())
				.originalFileName(imageInfo.getOriginalFileName())
				.build())
			.collect(Collectors.toList());

		List<ApplicationAnswerFile> savedFile = applicationAnswerFileRepository.saveAll(filesToSave);

		savedAnswer.setApplicationFiles(savedFile);

		return savedAnswer;
	}

}
