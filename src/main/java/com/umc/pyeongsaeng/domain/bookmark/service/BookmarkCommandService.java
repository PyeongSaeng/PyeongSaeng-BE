package com.umc.pyeongsaeng.domain.bookmark.service;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.CreatedBookmarkDTO;

public interface BookmarkCommandService {
	CreatedBookmarkDTO createBookmark(Long jobPostId, Long seniorProfileId);

	void deleteBookmark(Long jobPostId, Long seniorProfileId);
}
