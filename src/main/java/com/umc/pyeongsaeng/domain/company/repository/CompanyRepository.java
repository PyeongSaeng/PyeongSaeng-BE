package com.umc.pyeongsaeng.domain.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.umc.pyeongsaeng.domain.company.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
