package com.umc.pyeongsaeng.domain.bookmark.service;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.BookmarkSummaryListDTO;

public interface BookmarkQueryService {

	BookmarkSummaryListDTO getBookmarkSummaryList(Long seniorProfileId);
}
