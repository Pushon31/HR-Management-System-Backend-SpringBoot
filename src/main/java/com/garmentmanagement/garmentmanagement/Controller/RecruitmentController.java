package com.garmentmanagement.garmentmanagement.Controller;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    // ==================== JOB POSTING ENDPOINTS ====================

    /**
     * Create a new job
     */
    @PostMapping("/job-postings")
    public ResponseEntity<JobPostingDto> createJobPosting(@RequestBody JobPostingDto jobPostingDto) {
        JobPostingDto createdJob = recruitmentService.createJobPosting(jobPostingDto);
        return ResponseEntity.ok(createdJob);
    }

    /**
     * update job posting
     */
    @PutMapping("/job-postings/{id}")
    public ResponseEntity<JobPostingDto> updateJobPosting(
            @PathVariable Long id,
            @RequestBody JobPostingDto jobPostingDto) {
        JobPostingDto updatedJob = recruitmentService.updateJobPosting(id, jobPostingDto);
        return ResponseEntity.ok(updatedJob);
    }

    /**
     * get job posting by id
     */
    @GetMapping("/job-postings/{id}")
    public ResponseEntity<JobPostingDto> getJobPosting(@PathVariable Long id) {
        JobPostingDto jobPosting = recruitmentService.getJobPosting(id);
        return ResponseEntity.ok(jobPosting);
    }

    /**
     *  get job posting by id Job Code
     *  */
    @GetMapping("/job-postings/code/{jobCode}")
    public ResponseEntity<JobPostingDto> getJobPostingByCode(@PathVariable String jobCode) {
        JobPostingDto jobPosting = recruitmentService.getJobPostingByCode(jobCode);
        return ResponseEntity.ok(jobPosting);
    }

    /**
     * ALl job posting
     */
    @GetMapping("/job-postings")
    public ResponseEntity<List<JobPostingDto>> getAllJobPostings() {
        List<JobPostingDto> jobPostings = recruitmentService.getAllJobPostings();
        return ResponseEntity.ok(jobPostings);
    }

    /**
     * Active Job Postings
     */
    @GetMapping("/job-postings/active")
    public ResponseEntity<List<JobPostingDto>> getActiveJobPostings() {
        List<JobPostingDto> jobPostings = recruitmentService.getActiveJobPostings();
        return ResponseEntity.ok(jobPostings);
    }

    /**
     * get Departmentwise Job Postings
     */
    @GetMapping("/job-postings/department/{departmentId}")
    public ResponseEntity<List<JobPostingDto>> getJobPostingsByDepartment(@PathVariable Long departmentId) {
        List<JobPostingDto> jobPostings = recruitmentService.getJobPostingsByDepartment(departmentId);
        return ResponseEntity.ok(jobPostings);
    }

    /**
     * Close Job Posting Close
     */
    @PutMapping("/job-postings/{id}/close")
    public ResponseEntity<Void> closeJobPosting(@PathVariable Long id) {
        recruitmentService.closeJobPosting(id);
        return ResponseEntity.ok().build();
    }

    // ==================== CANDIDATE ENDPOINTS ====================

    /**
     * create new Candidate
     */
    @PostMapping("/candidates")
    public ResponseEntity<CandidateDto> createCandidate(@RequestBody CandidateDto candidateDto) {
        CandidateDto createdCandidate = recruitmentService.createCandidate(candidateDto);
        return ResponseEntity.ok(createdCandidate);
    }

    /**
     * update Candidate
     */
    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateDto> updateCandidate(
            @PathVariable Long id,
            @RequestBody CandidateDto candidateDto) {
        CandidateDto updatedCandidate = recruitmentService.updateCandidate(id, candidateDto);
        return ResponseEntity.ok(updatedCandidate);
    }

    /**
     * ID diye Candidate pawa
     */
    @GetMapping("/candidates/{id}")
    public ResponseEntity<CandidateDto> getCandidate(@PathVariable Long id) {
        CandidateDto candidate = recruitmentService.getCandidate(id);
        return ResponseEntity.ok(candidate);
    }

    /**
     * Email diye Candidate pawa
     */
    @GetMapping("/candidates/email/{email}")
    public ResponseEntity<CandidateDto> getCandidateByEmail(@PathVariable String email) {
        CandidateDto candidate = recruitmentService.getCandidateByEmail(email);
        return ResponseEntity.ok(candidate);
    }

    /**
     * getAll candidates
     */
    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateDto>> getAllCandidates() {
        List<CandidateDto> candidates = recruitmentService.getAllCandidates();
        return ResponseEntity.ok(candidates);
    }

    /**
     * Status wise Candidates
     */
    @GetMapping("/candidates/status/{status}")
    public ResponseEntity<List<CandidateDto>> getCandidatesByStatus(@PathVariable String status) {
        List<CandidateDto> candidates = recruitmentService.getCandidatesByStatus(status);
        return ResponseEntity.ok(candidates);
    }

    /**
     * Skill wise Candidates Search
     */
    @GetMapping("/candidates/search/skill")
    public ResponseEntity<List<CandidateDto>> searchCandidatesBySkill(@RequestParam String skill) {
        List<CandidateDto> candidates = recruitmentService.searchCandidatesBySkill(skill);
        return ResponseEntity.ok(candidates);
    }

    // ==================== APPLICATION ENDPOINTS ====================

    /**
     * New Application Submit
     */
    @PostMapping("/applications")
    public ResponseEntity<ApplicationDto> submitApplication(@RequestBody ApplicationDto applicationDto) {
        ApplicationDto application = recruitmentService.submitApplication(applicationDto);
        return ResponseEntity.ok(application);
    }

    /**
     *  get details by Application ID
     */
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationDto> getApplication(@PathVariable Long id) {
        ApplicationDto application = recruitmentService.getApplication(id);
        return ResponseEntity.ok(application);
    }

    /**
     *Job-wise Applications
     */
    @GetMapping("/applications/job/{jobPostingId}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByJob(@PathVariable Long jobPostingId) {
        List<ApplicationDto> applications = recruitmentService.getApplicationsByJob(jobPostingId);
        return ResponseEntity.ok(applications);
    }

    /**
     * Candidate wise Applications
     */
    @GetMapping("/applications/candidate/{candidateId}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByCandidate(@PathVariable Long candidateId) {
        List<ApplicationDto> applications = recruitmentService.getApplicationsByCandidate(candidateId);
        return ResponseEntity.ok(applications);
    }

    /**
     *  Application Status update
     */
    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationDto> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String notes) {
        ApplicationDto application = recruitmentService.updateApplicationStatus(id, status, notes);
        return ResponseEntity.ok(application);
    }

    /**
     * Status onujayi Applications
     */
    @GetMapping("/applications/status/{status}")
    public ResponseEntity<List<ApplicationDto>> getApplicationsByStatus(@PathVariable String status) {
        List<ApplicationDto> applications = recruitmentService.getApplicationsByStatus(status);
        return ResponseEntity.ok(applications);
    }

    // ==================== INTERVIEW ENDPOINTS ====================

    /**
     * Interview Schedule
     */
    @PostMapping("/interviews")
    public ResponseEntity<InterviewDto> scheduleInterview(@RequestBody InterviewDto interviewDto) {
        InterviewDto interview = recruitmentService.scheduleInterview(interviewDto);
        return ResponseEntity.ok(interview);
    }

    /**
     * update Interview
     */
    @PutMapping("/interviews/{id}")
    public ResponseEntity<InterviewDto> updateInterview(
            @PathVariable Long id,
            @RequestBody InterviewDto interviewDto) {
        InterviewDto interview = recruitmentService.updateInterview(id, interviewDto);
        return ResponseEntity.ok(interview);
    }

    /**
     * Interview ID diye interview get */
    @GetMapping("/interviews/{id}")
    public ResponseEntity<InterviewDto> getInterview(@PathVariable Long id) {
        InterviewDto interview = recruitmentService.getInterview(id);
        return ResponseEntity.ok(interview);
    }

    /**
     *  Application wise Interviews
     */
    @GetMapping("/interviews/application/{applicationId}")
    public ResponseEntity<List<InterviewDto>> getInterviewsByApplication(@PathVariable Long applicationId) {
        List<InterviewDto> interviews = recruitmentService.getInterviewsByApplication(applicationId);
        return ResponseEntity.ok(interviews);
    }

    /**
     * Upcoming Interviews
     */
    @GetMapping("/interviews/upcoming")
    public ResponseEntity<List<InterviewDto>> getUpcomingInterviews() {
        List<InterviewDto> interviews = recruitmentService.getUpcomingInterviews();
        return ResponseEntity.ok(interviews);
    }

    /**
     * Interviewer wise Interviews
     */
    @GetMapping("/interviews/interviewer/{interviewerId}")
    public ResponseEntity<List<InterviewDto>> getInterviewsByInterviewer(@PathVariable Long interviewerId) {
        List<InterviewDto> interviews = recruitmentService.getInterviewsByInterviewer(interviewerId);
        return ResponseEntity.ok(interviews);
    }

    /**
     * Interview Feedback Submit
     */
    @PutMapping("/interviews/{id}/feedback")
    public ResponseEntity<InterviewDto> submitInterviewFeedback(
            @PathVariable Long id,
            @RequestParam String feedback,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String notes) {
        InterviewDto interview = recruitmentService.submitInterviewFeedback(id, feedback, rating, notes);
        return ResponseEntity.ok(interview);
    }

    // ==================== OFFER LETTER ENDPOINTS ====================

    /**
     * Offer Letter Generate
     */
    @PostMapping("/offer-letters")
    public ResponseEntity<OfferLetterDto> generateOfferLetter(@RequestBody OfferLetterDto offerLetterDto) {
        OfferLetterDto offerLetter = recruitmentService.generateOfferLetter(offerLetterDto);
        return ResponseEntity.ok(offerLetter);
    }

    /**
     *  Offer Letter ID diye  Details
     */
    @GetMapping("/offer-letters/{id}")
    public ResponseEntity<OfferLetterDto> getOfferLetter(@PathVariable Long id) {
        OfferLetterDto offerLetter = recruitmentService.getOfferLetter(id);
        return ResponseEntity.ok(offerLetter);
    }

    /**
     * Application ID diye Offer Letter pawa
     */
    @GetMapping("/offer-letters/application/{applicationId}")
    public ResponseEntity<OfferLetterDto> getOfferLetterByApplication(@PathVariable Long applicationId) {
        OfferLetterDto offerLetter = recruitmentService.getOfferLetterByApplication(applicationId);
        return ResponseEntity.ok(offerLetter);
    }

    /**
     * Candidate-wise Offer Letters
     */
    @GetMapping("/offer-letters/candidate/{candidateId}")
    public ResponseEntity<List<OfferLetterDto>> getOfferLettersByCandidate(@PathVariable Long candidateId) {
        List<OfferLetterDto> offerLetters = recruitmentService.getOfferLettersByCandidate(candidateId);
        return ResponseEntity.ok(offerLetters);
    }

    /**
     * updatee status(Accept/Reject)
     */
    @PutMapping("/offer-letters/{id}/status")
    public ResponseEntity<OfferLetterDto> updateOfferStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String responseNotes) {
        OfferLetterDto offerLetter = recruitmentService.updateOfferStatus(id, status, responseNotes);
        return ResponseEntity.ok(offerLetter);
    }

    // ==================== REPORTS & DASHBOARD ENDPOINTS ====================

    /**
     * Recruitment Dashboard Data get
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getRecruitmentDashboard() {
        Map<String, Object> dashboard = recruitmentService.getRecruitmentDashboard();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Application Statistics get
     */
    @GetMapping("/reports/application-stats")
    public ResponseEntity<Map<String, Long>> getApplicationStatistics() {
        Map<String, Long> stats = recruitmentService.getApplicationStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Interview Statistics get
     */
    @GetMapping("/reports/interview-stats")
    public ResponseEntity<Map<String, Long>> getInterviewStatistics() {
        Map<String, Long> stats = recruitmentService.getInterviewStatistics();
        return ResponseEntity.ok(stats);
    }
}