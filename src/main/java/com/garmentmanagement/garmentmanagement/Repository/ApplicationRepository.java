package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByCandidateId(Long candidateId);
    List<Application> findByJobPostingId(Long jobPostingId);
    List<Application> findByStatus(Application.ApplicationStatus status);

    @Query("SELECT a FROM Application a WHERE a.candidate.id = :candidateId AND a.jobPosting.id = :jobPostingId")
    Optional<Application> findByCandidateAndJobPosting(@Param("candidateId") Long candidateId,
                                                       @Param("jobPostingId") Long jobPostingId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobPosting.id = :jobPostingId")
    Long countApplicationsByJobPosting(@Param("jobPostingId") Long jobPostingId);

    @Query("SELECT a FROM Application a WHERE a.jobPosting.department.id = :departmentId")
    List<Application> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
    List<Object[]> countApplicationsByStatus();
}