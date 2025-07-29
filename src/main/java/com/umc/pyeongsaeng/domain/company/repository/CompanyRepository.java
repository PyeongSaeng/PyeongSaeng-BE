package com.umc.pyeongsaeng.domain.company.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.company.enums.CompanyStatus;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
	Optional<Company> findByUsername(String username);
	boolean existsByUsername(String username);
	boolean existsByBusinessNo(String businessNo);
	boolean existsByPhone(String phone);
	List<Company> findByStatusAndWithdrawnAtBefore(CompanyStatus status, LocalDateTime withdrawnAt);
}
