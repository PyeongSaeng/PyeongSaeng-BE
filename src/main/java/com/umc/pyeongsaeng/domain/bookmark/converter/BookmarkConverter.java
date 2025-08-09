package com.umc.pyeongsaeng.domain.bookmark.converter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.BookmarkSummaryDTO;
import com.umc.pyeongsaeng.domain.bookmark.entity.Bookmark;
import com.umc.pyeongsaeng.domain.job.dto.response.JobPostResponseDTO.JobPostDetailDTO;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.service.JobPostQueryService;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class BookmarkConverter {

	private final JobPostQueryService jobPostQueryService;

	public BookmarkSummaryDTO toSummaryDTO(Bookmark bookmark, Long seniorProfileId) {
		JobPost jobPost = bookmark.getJobPost();
		Long jobPostId = jobPost.getId();

		JobPostDetailDTO jobPostDetailDTO = jobPostQueryService.getJobPostDetail(jobPostId, seniorProfileId);

		return BookmarkSummaryDTO.builder()
			.bookmarkId(bookmark.getId())
			.jobPostDetailDTO(jobPostDetailDTO)
			.build();
	}

	public List<BookmarkSummaryDTO> toSummaryDTOList(List<Bookmark> bookmarks, Long seniorProfileId) {
		return bookmarks.stream()
			.map(bookmark -> toSummaryDTO(bookmark, seniorProfileId))
			.toList();
	}

}

