package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findByJobCode(String jobCode);
    List<JobPosting> findByStatus(JobPosting.JobStatus status);
    List<JobPosting> findByDepartmentId(Long departmentId);

    @Query("SELECT jp FROM JobPosting jp WHERE jp.applicationDeadline >= :today AND jp.status = 'OPEN'")
    List<JobPosting> findActiveJobPostings(@Param("today") LocalDate today);

    @Query("SELECT jp FROM JobPosting jp WHERE jp.employmentType = :employmentType")
    List<JobPosting> findByEmploymentType(@Param("employmentType") JobPosting.EmploymentType employmentType);

    @Query("SELECT COUNT(jp) FROM JobPosting jp WHERE jp.status = 'OPEN'")
    Long countOpenPositions();

    boolean existsByJobCode(String jobCode);
}