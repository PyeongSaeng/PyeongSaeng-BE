package com.umc.pyeongsaeng.domain.bookmark.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.pyeongsaeng.domain.bookmark.dto.response.BookmarkResponseDTO;
import com.umc.pyeongsaeng.domain.bookmark.entity.Bookmark;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	List<Bookmark> findAllBySeniorProfile_SeniorId(Long seniorId);
	Optional<Bookmark> findByJobPostIdAndSeniorProfile_SeniorId(Long jobPostId, Long seniorProfileId);

}
