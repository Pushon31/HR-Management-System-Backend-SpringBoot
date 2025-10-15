package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.OfferLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfferLetterRepository extends JpaRepository<OfferLetter, Long> {

    Optional<OfferLetter> findByApplicationId(Long applicationId);
    Optional<OfferLetter> findByOfferCode(String offerCode);
    List<OfferLetter> findByStatus(OfferLetter.OfferStatus status);

    @Query("SELECT o FROM OfferLetter o WHERE o.application.candidate.id = :candidateId")
    List<OfferLetter> findByCandidateId(@Param("candidateId") Long candidateId);

    @Query("SELECT COUNT(o) FROM OfferLetter o WHERE o.status = 'PENDING'")
    Long countPendingOffers();

    boolean existsByOfferCode(String offerCode);
}