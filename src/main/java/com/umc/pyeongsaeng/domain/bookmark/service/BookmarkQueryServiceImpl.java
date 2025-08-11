package com.umc.pyeongsaeng.domain.bookmark.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.bookmark.converter.BookmarkConverter;
import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.BookmarkSummaryListDTO;
import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.BookmarkSummaryDTO;
import com.umc.pyeongsaeng.domain.bookmark.entity.Bookmark;
import com.umc.pyeongsaeng.domain.bookmark.repository.BookmarkRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkQueryServiceImpl implements BookmarkQueryService {

	private final BookmarkRepository bookmarkRepository;
	private final BookmarkConverter bookmarkConverter;

	@Override
	public BookmarkSummaryListDTO getBookmarkSummaryList(Long seniorProfileId) {
		List<Bookmark> bookmarks = bookmarkRepository.findAllBySeniorProfile_SeniorIdOrderByUpdatedAtDesc(seniorProfileId);;

		List<BookmarkSummaryDTO> bookmarkSummaryDTOList = bookmarkConverter.toSummaryDTOList(bookmarks, seniorProfileId);

		return BookmarkSummaryListDTO.builder()
			.bookmarkSummaryDTOList(bookmarkSummaryDTOList)
			.build();
	}
}
