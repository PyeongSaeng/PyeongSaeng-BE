package com.umc.pyeongsaeng.domain.senior.service;

import static com.umc.pyeongsaeng.domain.senior.converter.SeniorQuestionConverter.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.senior.converter.SeniorQuestionConverter;
import com.umc.pyeongsaeng.domain.senior.dto.response.SeniorQuestionResponseDTO;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestion;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorQuestionAnswer;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorQuestionAnswerRepository;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorQuestionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeniorQuestionQueryServiceImpl implements SeniorQuestionQueryService {

	private final SeniorQuestionRepository seniorQuestionRepository;
	private final SeniorQuestionAnswerRepository seniorQuestionAnswerRepository;

	@Override
	public List<SeniorQuestionResponseDTO.QuestionAnswerResponseDTO> getAllSeniorQuestionsWithAnswers(Long seniorProfileId) {

		List<SeniorQuestion> questions = seniorQuestionRepository.findAll();

		Map<Long, SeniorQuestionAnswer> answerMap = seniorQuestionAnswerRepository
			.findBySeniorProfile_SeniorId(seniorProfileId)
			.stream()
			.collect(Collectors.toMap(
				answer -> answer.getQuestion().getId(),
				answer -> answer
			));

		return questions.stream()
			.map(q -> SeniorQuestionConverter.toQuestionAnswerDTO(q, answerMap.get(q.getId())))
			.toList();
	}

}
