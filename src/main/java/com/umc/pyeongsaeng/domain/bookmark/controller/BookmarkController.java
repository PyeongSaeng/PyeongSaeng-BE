package com.umc.pyeongsaeng.domain.bookmark.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.BookmarkSummaryListDTO;
import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.CreatedBookmarkDTO;
import com.umc.pyeongsaeng.domain.bookmark.service.BookmarkCommandService;
import com.umc.pyeongsaeng.domain.bookmark.service.BookmarkQueryService;
import com.umc.pyeongsaeng.global.apiPayload.ApiResponse;
import com.umc.pyeongsaeng.global.apiPayload.code.status.SuccessStatus;
import com.umc.pyeongsaeng.global.security.CustomUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

	private final BookmarkCommandService bookmarkCommandService;
	private final BookmarkQueryService bookmarkQueryService;

	@Operation(summary = "[시니어] 추천/상세 페이지- 채용공고 저장 버튼", description = "시니어 본인이 특정 채용공고를 북마크(저장)합니다.")
	@PostMapping("/{jobPostId}")
	public ApiResponse<CreatedBookmarkDTO> createBookmark(@PathVariable Long jobPostId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		CreatedBookmarkDTO bookmarkDTO = bookmarkCommandService.createBookmark(jobPostId, userDetails.getId());
		SuccessStatus status = bookmarkDTO.isUpdated() ? SuccessStatus.BOOKMARK_ALREADY_UPDATED : SuccessStatus.BOOKMARK_CREATED;
		return ApiResponse.of(status, bookmarkDTO);
	}

	@Operation(summary = "[시니어] 일자리 저장함 - 목록 조회", description = "시니어 본인 일자리 저장함(북마크) 목록을 조회합니다.")
	@GetMapping("/mine")
	public ApiResponse<BookmarkSummaryListDTO> getBookmarkSummaryList(@AuthenticationPrincipal CustomUserDetails userDetails){
		return ApiResponse.onSuccess(bookmarkQueryService.getBookmarkSummaryList(userDetails.getId()));
	}

	@Operation(summary = "[시니어] 일자리 저장함 - 북마크 삭제", description = "일자리 저장함에서 특정 채용공고 북마크를 삭제합니다.")
	@DeleteMapping("/{jobPostId}")
	public ApiResponse<SuccessStatus> deleteBookmark(@PathVariable Long jobPostId, @AuthenticationPrincipal CustomUserDetails userDetails) {
		bookmarkCommandService.deleteBookmark(jobPostId, userDetails.getUser().getId());
		return ApiResponse.onSuccess(SuccessStatus.BOOKMARK_DELETED);
	}

}
