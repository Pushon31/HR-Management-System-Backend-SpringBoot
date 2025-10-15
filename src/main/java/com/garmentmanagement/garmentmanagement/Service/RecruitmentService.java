package com.garmentmanagement.garmentmanagement.Service;

import com.garmentmanagement.garmentmanagement.DTO.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RecruitmentService {

    // ==================== JOB POSTING METHODS ====================
    JobPostingDto createJobPosting(JobPostingDto jobPostingDto);
    JobPostingDto updateJobPosting(Long id, JobPostingDto jobPostingDto);
    JobPostingDto getJobPosting(Long id);
    JobPostingDto getJobPostingByCode(String jobCode);
    List<JobPostingDto> getAllJobPostings();
    List<JobPostingDto> getActiveJobPostings();
    List<JobPostingDto> getJobPostingsByDepartment(Long departmentId);
    void closeJobPosting(Long id);

    // ==================== CANDIDATE METHODS ====================
    CandidateDto createCandidate(CandidateDto candidateDto);
    CandidateDto updateCandidate(Long id, CandidateDto candidateDto);
    CandidateDto getCandidate(Long id);
    CandidateDto getCandidateByEmail(String email);
    List<CandidateDto> getAllCandidates();
    List<CandidateDto> getCandidatesByStatus(String status);
    List<CandidateDto> searchCandidatesBySkill(String skill);

    // ==================== APPLICATION METHODS ====================
    ApplicationDto submitApplication(ApplicationDto applicationDto);
    ApplicationDto getApplication(Long id);
    List<ApplicationDto> getApplicationsByJob(Long jobPostingId);
    List<ApplicationDto> getApplicationsByCandidate(Long candidateId);
    ApplicationDto updateApplicationStatus(Long id, String status, String notes);
    List<ApplicationDto> getApplicationsByStatus(String status);

    // ==================== INTERVIEW METHODS ====================
    InterviewDto scheduleInterview(InterviewDto interviewDto);
    InterviewDto updateInterview(Long id, InterviewDto interviewDto);
    InterviewDto getInterview(Long id);
    List<InterviewDto> getInterviewsByApplication(Long applicationId);
    List<InterviewDto> getUpcomingInterviews();
    List<InterviewDto> getInterviewsByInterviewer(Long interviewerId);
    InterviewDto submitInterviewFeedback(Long id, String feedback, Integer rating, String notes);

    // ==================== OFFER LETTER METHODS ====================
    OfferLetterDto generateOfferLetter(OfferLetterDto offerLetterDto);
    OfferLetterDto getOfferLetter(Long id);
    OfferLetterDto getOfferLetterByApplication(Long applicationId);
    List<OfferLetterDto> getOfferLettersByCandidate(Long candidateId);
    OfferLetterDto updateOfferStatus(Long id, String status, String responseNotes);

    // ==================== REPORTS & DASHBOARD ====================
    Map<String, Object> getRecruitmentDashboard();
    Map<String, Long> getApplicationStatistics();
    Map<String, Long> getInterviewStatistics();
}