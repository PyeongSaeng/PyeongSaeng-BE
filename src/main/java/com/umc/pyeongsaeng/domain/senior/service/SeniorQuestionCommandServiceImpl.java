package com.umc.pyeongsaeng.domain.senior.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.senior.converter.SeniorQuestionConverter;
import com.umc.pyeongsaeng.domain.senior.dto.request.SeniorQuestionRequestDTO;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestion;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionAnswer;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionOption;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorQuestionAnswerRepository;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorQuestionOptionRepository;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorQuestionRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeniorQuestionCommandServiceImpl implements SeniorQuestionCommandService {

	private final SeniorProfileRepository seniorProfileRepository;
	private final SeniorQuestionRepository seniorQuestionRepository;
	private final SeniorQuestionOptionRepository seniorQuestionOptionRepository;
	private final SeniorQuestionAnswerRepository seniorQuestionAnswerRepository;

	@Override
	@Transactional
	public void saveOrUpdateAnswer(Long seniorProfileId, SeniorQuestionRequestDTO.AnswerRequestDTO request) {
		SeniorProfile profile = seniorProfileRepository.findById(seniorProfileId)
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_NOT_FOUND));

		SeniorQuestion question = seniorQuestionRepository.findById(request.getQuestionId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_QUESTION_NOT_FOUND));

		Optional<SeniorQuestionAnswer> existingAnswer =
			seniorQuestionAnswerRepository.findBySeniorProfile_SeniorIdAndQuestion_Id(seniorProfileId, request.getQuestionId());

		if (request.getSelectedOptionId() == null) {
			existingAnswer.ifPresent(answer -> {seniorQuestionAnswerRepository.delete(answer);});
			return;
		}

		SeniorQuestionOption option = seniorQuestionOptionRepository.findById(request.getSelectedOptionId())
			.orElseThrow(() -> new GeneralException(ErrorStatus.SENIOR_QUESTION_OPTION_NOT_FOUND));

		SeniorQuestionAnswer answer = existingAnswer.orElse(
			SeniorQuestionAnswer.builder()
				.seniorProfile(profile)
				.question(question)
				.build()
		);

		answer.updateSelectedOption(option);
		seniorQuestionAnswerRepository.save(answer);
	}

	@Override
	@Transactional
	public Long createQuestion(SeniorQuestionRequestDTO.QuestionRequestDTO request) {
		SeniorQuestion question = SeniorQuestionConverter.toSeniorQuestion(request);
		SeniorQuestion saved = seniorQuestionRepository.save(question);

		return saved.getId();
	}


}
