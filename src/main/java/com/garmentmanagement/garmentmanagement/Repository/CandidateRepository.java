package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findByNidNumber(String nidNumber);
    List<Candidate> findByStatus(Candidate.CandidateStatus status);

    @Query("SELECT c FROM Candidate c WHERE c.skills LIKE %:skill%")
    List<Candidate> findBySkill(@Param("skill") String skill);

    @Query("SELECT c FROM Candidate c WHERE c.experience = :experience")
    List<Candidate> findByExperience(@Param("experience") String experience);

    boolean existsByEmail(String email);
    boolean existsByNidNumber(String nidNumber);

    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.status = 'NEW'")
    Long countNewCandidates();
}