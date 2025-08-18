package com.umc.pyeongsaeng.domain.job.repository;

import com.umc.pyeongsaeng.domain.company.entity.Company;
import com.umc.pyeongsaeng.domain.job.entity.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long>, JobPostRepositoryCustom {
	Optional<JobPost> findByApplicationsId(Long jobPostId);

	void deleteByCompanyId(Long companyId);

	@Query("SELECT jp FROM JobPost jp WHERE jp.company = :company AND (jp.deadline > CURRENT_TIMESTAMP AND jp.state = 'RECRUITING')")
	Page<JobPost> findActiveJobPostsByCompany(Company company, PageRequest pageRequest);

	@Query("SELECT jp FROM JobPost jp WHERE jp.company = :company AND (jp.deadline <= CURRENT_TIMESTAMP OR jp.state = 'CLOSED')")
	Page<JobPost> findClosedJobPostsByCompany(Company company, PageRequest pageRequest);

	@Query(
		value = """
           SELECT jp.*
           FROM job_post jp
           INNER JOIN (
               SELECT
                   job_post_id,
                   COUNT(*) AS application_count
               FROM
                   application ap
               WHERE
                   ap.created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
               GROUP BY
                   job_post_id
           ) AS count_subquery ON jp.id = count_subquery.job_post_id
           WHERE jp.company_id = :#{#company.id} AND jp.state = 'RECRUITING'
           ORDER BY count_subquery.application_count DESC
            """,
		countQuery = """
            SELECT count(jp.id)
            FROM job_post jp
            INNER JOIN (
                SELECT job_post_id
                FROM application
                GROUP BY job_post_id
            ) AS count_subquery ON jp.id = count_subquery.job_post_id
            WHERE jp.company_id = :#{#company.id} AND jp.state = 'RECRUITING'
            """,
		nativeQuery = true
	)
	Page<JobPost> findActiveJobPostsByCompanyByPopularity(
		@Param("company") Company company,
		Pageable pageable
	);

}

