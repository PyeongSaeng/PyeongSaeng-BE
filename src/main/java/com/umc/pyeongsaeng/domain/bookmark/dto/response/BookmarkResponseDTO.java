package com.umc.pyeongsaeng.domain.bookmark.dto.response;

import java.util.List;

import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkResponseDTO {

	/**
	 * 북마크 생성 후 반환용 DTO
	 */
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class CreatedBookmarkDTO {
		private Long bookmarkId;
	}

	/**
	 * 북마크 단건 DTO
	 */
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class BookmarkSummaryDTO {
		private Long bookmarkId;
		private JobPostResponseDTO.JobPostDetailDTO jobPostDetailDTO;
	}

	/**
	 * 북마크 목록 조회용 DTO
	 */
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder
	public static class BookmarkSummaryListDTO {
		List<BookmarkSummaryDTO> bookmarkSummaryDTOList;
	}
}
