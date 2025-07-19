package com.umc.pyeongsaeng.domain.user.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.pyeongsaeng.domain.user.entity.User;
import com.umc.pyeongsaeng.domain.user.enums.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);
	boolean existsByUsername(String username);
	Optional<User> findByPhone(String phone);
	boolean existsByPhone(String phone);
	List<User> findByStatusAndWithdrawnAtBefore(UserStatus status, LocalDateTime dateTime);
}
