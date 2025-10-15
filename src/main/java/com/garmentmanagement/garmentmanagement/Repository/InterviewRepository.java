package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    List<Interview> findByApplicationId(Long applicationId);
    List<Interview> findByInterviewerId(Long interviewerId);
    List<Interview> findByStatus(Interview.InterviewStatus status);

    @Query("SELECT i FROM Interview i WHERE i.interviewDate BETWEEN :startDate AND :endDate")
    List<Interview> findInterviewsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT i FROM Interview i WHERE i.interviewDate >= :today AND i.status = 'SCHEDULED'")
    List<Interview> findUpcomingInterviews(@Param("today") LocalDateTime today);

    @Query("SELECT i FROM Interview i WHERE i.application.jobPosting.department.id = :departmentId")
    List<Interview> findByDepartmentId(@Param("departmentId") Long departmentId);
}