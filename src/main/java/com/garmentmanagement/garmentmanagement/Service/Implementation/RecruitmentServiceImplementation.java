package com.garmentmanagement.garmentmanagement.Service.Implementation;

import com.garmentmanagement.garmentmanagement.DTO.*;
import com.garmentmanagement.garmentmanagement.Entity.*;
import com.garmentmanagement.garmentmanagement.Repository.*;
import com.garmentmanagement.garmentmanagement.Service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RecruitmentServiceImplementation implements RecruitmentService {

    private final JobPostingRepository jobPostingRepo;
    private final CandidateRepository candidateRepo;
    private final ApplicationRepository applicationRepo;
    private final InterviewRepository interviewRepo;
    private final OfferLetterRepository offerLetterRepo;
    private final DepartmentRepository departmentRepo;
    private final EmployeeRepository employeeRepo;
    private final ModelMapper modelMapper;

    // ==================== JOB POSTING METHODS ====================

    @Override
    public JobPostingDto createJobPosting(JobPostingDto jobPostingDto) {
        // Check duplicate job code
        if (jobPostingRepo.existsByJobCode(jobPostingDto.getJobCode())) {
            throw new RuntimeException("Job code already exists: " + jobPostingDto.getJobCode());
        }

        JobPosting jobPosting = modelMapper.map(jobPostingDto, JobPosting.class);

        // Set department if provided
        if (jobPostingDto.getDepartmentId() != null) {
            Department department = departmentRepo.findById(jobPostingDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            jobPosting.setDepartment(department);
        }

        jobPosting.setPostedDate(LocalDate.now());
        jobPosting.setStatus(JobPosting.JobStatus.OPEN);

        JobPosting saved = jobPostingRepo.save(jobPosting);
        return convertToJobPostingDto(saved);
    }

    @Override
    public JobPostingDto updateJobPosting(Long id, JobPostingDto jobPostingDto) {
        JobPosting existing = jobPostingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        // Check duplicate job code for other postings
        if (!existing.getJobCode().equals(jobPostingDto.getJobCode()) &&
                jobPostingRepo.existsByJobCode(jobPostingDto.getJobCode())) {
            throw new RuntimeException("Job code already exists: " + jobPostingDto.getJobCode());
        }

        modelMapper.map(jobPostingDto, existing);

        // Update department if changed
        if (jobPostingDto.getDepartmentId() != null) {
            Department department = departmentRepo.findById(jobPostingDto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(department);
        }

        // Update enum status
        if (jobPostingDto.getStatus() != null) {
            existing.setStatus(JobPosting.JobStatus.valueOf(jobPostingDto.getStatus()));
        }

        JobPosting updated = jobPostingRepo.save(existing);
        return convertToJobPostingDto(updated);
    }

    @Override
    public JobPostingDto getJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));
        return convertToJobPostingDto(jobPosting);
    }

    @Override
    public JobPostingDto getJobPostingByCode(String jobCode) {
        JobPosting jobPosting = jobPostingRepo.findByJobCode(jobCode)
                .orElseThrow(() -> new RuntimeException("Job posting not found with code: " + jobCode));
        return convertToJobPostingDto(jobPosting);
    }

    @Override
    public List<JobPostingDto> getAllJobPostings() {
        return jobPostingRepo.findAll()
                .stream()
                .map(this::convertToJobPostingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDto> getActiveJobPostings() {
        return jobPostingRepo.findActiveJobPostings(LocalDate.now())
                .stream()
                .map(this::convertToJobPostingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobPostingDto> getJobPostingsByDepartment(Long departmentId) {
        return jobPostingRepo.findByDepartmentId(departmentId)
                .stream()
                .map(this::convertToJobPostingDto)
                .collect(Collectors.toList());
    }

    @Override
    public void closeJobPosting(Long id) {
        JobPosting jobPosting = jobPostingRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Job posting not found"));
        jobPosting.setStatus(JobPosting.JobStatus.CLOSED);
        jobPostingRepo.save(jobPosting);
    }

    // ==================== CANDIDATE METHODS ====================

    @Override
    public CandidateDto createCandidate(CandidateDto candidateDto) {
        // Check duplicate email and NID
        if (candidateRepo.existsByEmail(candidateDto.getEmail())) {
            throw new RuntimeException("Candidate already exists with email: " + candidateDto.getEmail());
        }
        if (candidateDto.getNidNumber() != null && candidateRepo.existsByNidNumber(candidateDto.getNidNumber())) {
            throw new RuntimeException("NID already exists: " + candidateDto.getNidNumber());
        }

        Candidate candidate = modelMapper.map(candidateDto, Candidate.class);
        candidate.setStatus(Candidate.CandidateStatus.NEW);

        Candidate saved = candidateRepo.save(candidate);
        return convertToCandidateDto(saved);
    }

    @Override
    public CandidateDto updateCandidate(Long id, CandidateDto candidateDto) {
        Candidate existing = candidateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Check duplicate email for other candidates
        if (!existing.getEmail().equals(candidateDto.getEmail()) &&
                candidateRepo.existsByEmail(candidateDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + candidateDto.getEmail());
        }

        modelMapper.map(candidateDto, existing);

        // Update enum status
        if (candidateDto.getStatus() != null) {
            existing.setStatus(Candidate.CandidateStatus.valueOf(candidateDto.getStatus()));
        }

        Candidate updated = candidateRepo.save(existing);
        return convertToCandidateDto(updated);
    }

    @Override
    public CandidateDto getCandidate(Long id) {
        Candidate candidate = candidateRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        return convertToCandidateDto(candidate);
    }

    @Override
    public CandidateDto getCandidateByEmail(String email) {
        Candidate candidate = candidateRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Candidate not found with email: " + email));
        return convertToCandidateDto(candidate);
    }

    @Override
    public List<CandidateDto> getAllCandidates() {
        return candidateRepo.findAll()
                .stream()
                .map(this::convertToCandidateDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto> getCandidatesByStatus(String status) {
        Candidate.CandidateStatus statusEnum = Candidate.CandidateStatus.valueOf(status);
        return candidateRepo.findByStatus(statusEnum)
                .stream()
                .map(this::convertToCandidateDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto> searchCandidatesBySkill(String skill) {
        return candidateRepo.findBySkill(skill)
                .stream()
                .map(this::convertToCandidateDto)
                .collect(Collectors.toList());
    }

    // ==================== APPLICATION METHODS ====================

    @Override
    public ApplicationDto submitApplication(ApplicationDto applicationDto) {
        Candidate candidate = candidateRepo.findById(applicationDto.getCandidateId())
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        JobPosting jobPosting = jobPostingRepo.findById(applicationDto.getJobPostingId())
                .orElseThrow(() -> new RuntimeException("Job posting not found"));

        // Check if already applied
        if (applicationRepo.findByCandidateAndJobPosting(candidate.getId(), jobPosting.getId()).isPresent()) {
            throw new RuntimeException("Candidate already applied for this job");
        }

        Application application = modelMapper.map(applicationDto, Application.class);
        application.setCandidate(candidate);
        application.setJobPosting(jobPosting);
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(Application.ApplicationStatus.APPLIED);

        Application saved = applicationRepo.save(application);
        return convertToApplicationDto(saved);
    }

    @Override
    public ApplicationDto getApplication(Long id) {
        Application application = applicationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return convertToApplicationDto(application);
    }

    @Override
    public List<ApplicationDto> getApplicationsByJob(Long jobPostingId) {
        return applicationRepo.findByJobPostingId(jobPostingId)
                .stream()
                .map(this::convertToApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationDto> getApplicationsByCandidate(Long candidateId) {
        return applicationRepo.findByCandidateId(candidateId)
                .stream()
                .map(this::convertToApplicationDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApplicationDto updateApplicationStatus(Long id, String status, String notes) {
        Application application = applicationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(Application.ApplicationStatus.valueOf(status));
        if (notes != null) {
            application.setNotes(notes);
        }

        Application updated = applicationRepo.save(application);
        return convertToApplicationDto(updated);
    }

    @Override
    public List<ApplicationDto> getApplicationsByStatus(String status) {
        Application.ApplicationStatus statusEnum = Application.ApplicationStatus.valueOf(status);
        return applicationRepo.findByStatus(statusEnum)
                .stream()
                .map(this::convertToApplicationDto)
                .collect(Collectors.toList());
    }

    // ==================== INTERVIEW METHODS ====================

    @Override
    public InterviewDto scheduleInterview(InterviewDto interviewDto) {
        Application application = applicationRepo.findById(interviewDto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Employee interviewer = null;
        if (interviewDto.getInterviewerId() != null) {
            interviewer = employeeRepo.findById(interviewDto.getInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Interviewer not found"));
        }

        Interview interview = modelMapper.map(interviewDto, Interview.class);
        interview.setApplication(application);
        interview.setInterviewer(interviewer);
        interview.setStatus(Interview.InterviewStatus.SCHEDULED);

        Interview saved = interviewRepo.save(interview);

        // Update application status to INTERVIEW_SCHEDULED
        application.setStatus(Application.ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepo.save(application);

        return convertToInterviewDto(saved);
    }

    @Override
    public InterviewDto updateInterview(Long id, InterviewDto interviewDto) {
        Interview existing = interviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        modelMapper.map(interviewDto, existing);

        // Update interviewer if changed
        if (interviewDto.getInterviewerId() != null) {
            Employee interviewer = employeeRepo.findById(interviewDto.getInterviewerId())
                    .orElseThrow(() -> new RuntimeException("Interviewer not found"));
            existing.setInterviewer(interviewer);
        }

        Interview updated = interviewRepo.save(existing);
        return convertToInterviewDto(updated);
    }

    @Override
    public InterviewDto getInterview(Long id) {
        Interview interview = interviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));
        return convertToInterviewDto(interview);
    }

    @Override
    public List<InterviewDto> getInterviewsByApplication(Long applicationId) {
        return interviewRepo.findByApplicationId(applicationId)
                .stream()
                .map(this::convertToInterviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getUpcomingInterviews() {
        return interviewRepo.findUpcomingInterviews(LocalDateTime.now())
                .stream()
                .map(this::convertToInterviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<InterviewDto> getInterviewsByInterviewer(Long interviewerId) {
        return interviewRepo.findByInterviewerId(interviewerId)
                .stream()
                .map(this::convertToInterviewDto)
                .collect(Collectors.toList());
    }

    @Override
    public InterviewDto submitInterviewFeedback(Long id, String feedback, Integer rating, String notes) {
        Interview interview = interviewRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        interview.setStatus(Interview.InterviewStatus.COMPLETED);
        interview.setFeedback(feedback);
        interview.setRating(rating);
        interview.setNotes(notes);

        Interview updated = interviewRepo.save(interview);

        // Update application status based on rating
        Application application = interview.getApplication();
        if (rating != null && rating >= 4) {
            application.setStatus(Application.ApplicationStatus.SELECTED);
        } else {
            application.setStatus(Application.ApplicationStatus.REJECTED);
            application.setRejectionReason("Interview performance not satisfactory");
        }
        applicationRepo.save(application);

        return convertToInterviewDto(updated);
    }

    // ==================== OFFER LETTER METHODS ====================

    @Override
    public OfferLetterDto generateOfferLetter(OfferLetterDto offerLetterDto) {
        Application application = applicationRepo.findById(offerLetterDto.getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // Check if offer already exists
        if (offerLetterRepo.findByApplicationId(application.getId()).isPresent()) {
            throw new RuntimeException("Offer letter already generated for this application");
        }

        // Generate unique offer code
        String offerCode = "OFFER-" + LocalDate.now().getYear() + "-" +
                String.format("%03d", offerLetterRepo.count() + 1);

        OfferLetter offerLetter = modelMapper.map(offerLetterDto, OfferLetter.class);
        offerLetter.setApplication(application);
        offerLetter.setOfferCode(offerCode);
        offerLetter.setOfferDate(LocalDate.now());
        offerLetter.setStatus(OfferLetter.OfferStatus.PENDING);

        offerLetter.calculateGrossSalary();

        OfferLetter saved = offerLetterRepo.save(offerLetter);

        // Update application status to OFFERED
        application.setStatus(Application.ApplicationStatus.OFFERED);
        applicationRepo.save(application);

        return convertToOfferLetterDto(saved);
    }

    @Override
    public OfferLetterDto getOfferLetter(Long id) {
        OfferLetter offerLetter = offerLetterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer letter not found"));
        return convertToOfferLetterDto(offerLetter);
    }

    @Override
    public OfferLetterDto getOfferLetterByApplication(Long applicationId) {
        OfferLetter offerLetter = offerLetterRepo.findByApplicationId(applicationId)
                .orElseThrow(() -> new RuntimeException("Offer letter not found for this application"));
        return convertToOfferLetterDto(offerLetter);
    }

    @Override
    public List<OfferLetterDto> getOfferLettersByCandidate(Long candidateId) {
        return offerLetterRepo.findByCandidateId(candidateId)
                .stream()
                .map(this::convertToOfferLetterDto)
                .collect(Collectors.toList());
    }

    @Override
    public OfferLetterDto updateOfferStatus(Long id, String status, String responseNotes) {
        OfferLetter offerLetter = offerLetterRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer letter not found"));

        offerLetter.setStatus(OfferLetter.OfferStatus.valueOf(status));
        offerLetter.setResponseNotes(responseNotes);
        offerLetter.setResponseDate(LocalDate.now());

        OfferLetter updated = offerLetterRepo.save(offerLetter);

        // Update application status based on offer response
        Application application = offerLetter.getApplication();
        if (status.equals("ACCEPTED")) {
            application.setStatus(Application.ApplicationStatus.HIRED);

            // Here you can trigger onboarding process
            // Create employee record, etc.
        } else if (status.equals("REJECTED")) {
            application.setStatus(Application.ApplicationStatus.REJECTED);
        }
        applicationRepo.save(application);

        return convertToOfferLetterDto(updated);
    }

    // ==================== REPORTS & DASHBOARD ====================

    @Override
    public Map<String, Object> getRecruitmentDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        dashboard.put("totalJobPostings", jobPostingRepo.count());
        dashboard.put("openPositions", jobPostingRepo.countOpenPositions());
        dashboard.put("totalCandidates", candidateRepo.count());
        dashboard.put("newCandidates", candidateRepo.countNewCandidates());
        dashboard.put("totalApplications", applicationRepo.count());
        dashboard.put("pendingOffers", offerLetterRepo.countPendingOffers());

        return dashboard;
    }

    @Override
    public Map<String, Long> getApplicationStatistics() {
        Map<String, Long> stats = new HashMap<>();

        List<Object[]> statusCounts = applicationRepo.countApplicationsByStatus();
        for (Object[] result : statusCounts) {
            String status = result[0].toString();
            Long count = (Long) result[1];
            stats.put(status, count);
        }

        return stats;
    }

    @Override
    public Map<String, Long> getInterviewStatistics() {
        Map<String, Long> stats = new HashMap<>();

        // Count interviews by status
        for (Interview.InterviewStatus status : Interview.InterviewStatus.values()) {
            Long count = (long) interviewRepo.findByStatus(status).size();
            stats.put(status.name(), count);
        }

        return stats;
    }

    // ==================== DTO CONVERSION METHODS ====================

    private JobPostingDto convertToJobPostingDto(JobPosting jobPosting) {
        JobPostingDto dto = modelMapper.map(jobPosting, JobPostingDto.class);

        if (jobPosting.getDepartment() != null) {
            dto.setDepartmentId(jobPosting.getDepartment().getId());
            dto.setDepartmentName(jobPosting.getDepartment().getName());
        }

        if (jobPosting.getStatus() != null) {
            dto.setStatus(jobPosting.getStatus().name());
        }

        if (jobPosting.getEmploymentType() != null) {
            dto.setEmploymentType(jobPosting.getEmploymentType().name());
        }

        // Add application statistics
        Long totalApplications = applicationRepo.countApplicationsByJobPosting(jobPosting.getId());
        dto.setTotalApplications(totalApplications.intValue());

        return dto;
    }

    private CandidateDto convertToCandidateDto(Candidate candidate) {
        CandidateDto dto = modelMapper.map(candidate, CandidateDto.class);

        if (candidate.getGender() != null) {
            dto.setGender(candidate.getGender().name());
        }

        if (candidate.getStatus() != null) {
            dto.setStatus(candidate.getStatus().name());
        }

        // Add application count
        Long totalApplications = (long) applicationRepo.findByCandidateId(candidate.getId()).size();
        dto.setTotalApplications(totalApplications.intValue());

        return dto;
    }

    private ApplicationDto convertToApplicationDto(Application application) {
        ApplicationDto dto = modelMapper.map(application, ApplicationDto.class);

        if (application.getCandidate() != null) {
            dto.setCandidateId(application.getCandidate().getId());
            dto.setCandidateName(application.getCandidate().getFirstName() + " " + application.getCandidate().getLastName());
            dto.setCandidateEmail(application.getCandidate().getEmail());
            dto.setCandidatePhone(application.getCandidate().getPhone());
        }

        if (application.getJobPosting() != null) {
            dto.setJobPostingId(application.getJobPosting().getId());
            dto.setJobTitle(application.getJobPosting().getJobTitle());
            dto.setJobCode(application.getJobPosting().getJobCode());

            if (application.getJobPosting().getDepartment() != null) {
                dto.setDepartmentName(application.getJobPosting().getDepartment().getName());
            }
        }

        if (application.getStatus() != null) {
            dto.setStatus(application.getStatus().name());
        }

        // Check if interview exists
        List<Interview> interviews = interviewRepo.findByApplicationId(application.getId());
        dto.setHasInterview(!interviews.isEmpty());
        if (!interviews.isEmpty()) {
            dto.setInterviewDate(interviews.get(0).getInterviewDate());
        }

        return dto;
    }

    private InterviewDto convertToInterviewDto(Interview interview) {
        InterviewDto dto = modelMapper.map(interview, InterviewDto.class);

        if (interview.getApplication() != null) {
            Application application = interview.getApplication();
            dto.setApplicationId(application.getId());
            dto.setCandidateId(application.getCandidate().getId());
            dto.setCandidateName(application.getCandidate().getFirstName() + " " + application.getCandidate().getLastName());
            dto.setCandidateEmail(application.getCandidate().getEmail());
            dto.setJobPostingId(application.getJobPosting().getId());
            dto.setJobTitle(application.getJobPosting().getJobTitle());
        }

        if (interview.getInterviewer() != null) {
            dto.setInterviewerId(interview.getInterviewer().getId());
            dto.setInterviewerName(interview.getInterviewer().getFirstName() + " " + interview.getInterviewer().getLastName());
        }

        if (interview.getStatus() != null) {
            dto.setStatus(interview.getStatus().name());
        }

        return dto;
    }

    private OfferLetterDto convertToOfferLetterDto(OfferLetter offerLetter) {
        OfferLetterDto dto = modelMapper.map(offerLetter, OfferLetterDto.class);

        if (offerLetter.getApplication() != null) {
            Application application = offerLetter.getApplication();
            dto.setApplicationId(application.getId());
            dto.setCandidateId(application.getCandidate().getId());
            dto.setCandidateName(application.getCandidate().getFirstName() + " " + application.getCandidate().getLastName());
            dto.setCandidateEmail(application.getCandidate().getEmail());
            dto.setJobPostingId(application.getJobPosting().getId());
            dto.setJobTitle(application.getJobPosting().getJobTitle());
        }

        if (offerLetter.getStatus() != null) {
            dto.setStatus(offerLetter.getStatus().name());
        }

        return dto;
    }
}