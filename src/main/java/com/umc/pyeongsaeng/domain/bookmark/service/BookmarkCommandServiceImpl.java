package com.umc.pyeongsaeng.domain.bookmark.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO.CreatedBookmarkDTO;
import com.umc.pyeongsaeng.domain.bookmark.entity.Bookmark;
import com.umc.pyeongsaeng.domain.bookmark.repository.BookmarkRepository;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import com.umc.pyeongsaeng.domain.job.repository.JobPostRepository;
import com.umc.pyeongsaeng.domain.senior.entity.SeniorProfile;
import com.umc.pyeongsaeng.domain.senior.repository.SeniorProfileRepository;
import com.umc.pyeongsaeng.global.apiPayload.code.exception.GeneralException;
import com.umc.pyeongsaeng.global.apiPayload.code.status.ErrorStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkCommandServiceImpl implements BookmarkCommandService {
	private final BookmarkRepository bookmarkRepository;
	private final JobPostRepository jobPostRepository;
	private final SeniorProfileRepository seniorProfileRepository;

	@Override
	public CreatedBookmarkDTO createBookmark(Long jobPostId, Long seniorProfileId) {
		JobPost jobPost = jobPostRepository.findById(jobPostId).orElseThrow(()-> new GeneralException(ErrorStatus.INVALID_JOB_POST_ID));
		SeniorProfile seniorProfile = seniorProfileRepository.findBySeniorId(seniorProfileId).orElseThrow(()->new GeneralException(ErrorStatus.SENIOR_PROFILE_NOT_FOUND));

		Optional<Bookmark> existing = bookmarkRepository.findByJobPost_IdAndSeniorProfile_SeniorId(jobPostId, seniorProfileId);

		if (existing.isPresent()) {
			Bookmark bookmark = existing.get();
			bookmark.refreshUpdatedAt();
			bookmarkRepository.save(bookmark);
			return CreatedBookmarkDTO.builder()
				.bookmarkId(bookmark.getId())
				.updated(true)
				.build();
		}

		Bookmark bookmark = Bookmark.builder().jobPost(jobPost).seniorProfile(seniorProfile).build();

		bookmarkRepository.save(bookmark);
		return CreatedBookmarkDTO.builder()
			.bookmarkId(bookmark.getId())
			.updated(false)
			.build();
	}

	@Override
	@Transactional
	public void deleteBookmark(Long jobPostId, Long seniorProfileId) {
		Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByJobPost_IdAndSeniorProfile_SeniorId(jobPostId, seniorProfileId);

		if (bookmarkOptional.isPresent()) {
			bookmarkRepository.delete(bookmarkOptional.get());
		} else {
			throw new GeneralException(ErrorStatus.BOOKMARK_NOT_FOUND);
		}
	}
}
