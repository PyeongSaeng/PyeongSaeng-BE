package com.umc.pyeongsaeng.domain.application.util;

public class OpenAiPromptBuilder {
	public static String buildPrompt(String experience, String jobDescription, String question) {
		return String.format("""
            아래는 한 구직자의 지원서 질문입니다. 질문에 대해 적절한 답변을 작성해주세요.

            [지원자 경력]
            %s

            [공고 설명]
            %s

            [질문]
            %s

            [답변]
            """, experience, jobDescription, question);
	}
}
