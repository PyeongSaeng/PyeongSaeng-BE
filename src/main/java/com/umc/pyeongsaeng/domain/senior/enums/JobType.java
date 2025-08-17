package com.umc.pyeongsaeng.domain.senior.enums;

import java.util.List;

public enum JobType {
	HOUSEWIFE(List.of("요양보호사", "활동보조", "돌봄", "도우미", "산후 도우미", "주부")),
	EMPLOYEE(List.of("사무보조", "문서정리", "자료입력", "비서업무", "콜센터상담", "고객응대")),
	PUBLIC_OFFICER(List.of("민원안내", "공공기관 보조", "행정지원", "자료정리", "도서관 사서보조")),
	PROFESSIONAL(List.of("회계보조", "서류작성", "강의보조", "기획", "자문", "멘토링")),
	ARTIST(List.of("문화해설사", "그림지도", "악기지도", "공예지도", "전시안내", "사진촬영 보조")),
	BUSINESS_OWNER(List.of("배달보조", "매장관리", "행사운영", "시장도우미", "판매지원", "홍보활동")),
	ETC(List.of("경비", "시설관리", "청소미화", "단순노무", "주차관리", "도우미"));

	private final List<String> relatedSeniorJobs;

	JobType(List<String> relatedSeniorJobs) {
		this.relatedSeniorJobs = relatedSeniorJobs;
	}

	public List<String> getRelatedSeniorJobs() {
		return relatedSeniorJobs;
	}
}
