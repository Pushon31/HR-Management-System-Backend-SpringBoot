package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {

    Optional<LeaveType> findByName(String name);
    Optional<LeaveType> findByCode(String code);
    List<LeaveType> findByIsActiveTrue();
    List<LeaveType> findByCategory(LeaveType.LeaveCategory category);
    boolean existsByName(String name);
    boolean existsByCode(String code);

    // Find active leave types with category
    @Query("SELECT lt FROM LeaveType lt WHERE lt.isActive = true AND lt.category = :category")
    List<LeaveType> findActiveByCategory(LeaveType.LeaveCategory category);

    // Count active leave types
    @Query("SELECT COUNT(lt) FROM LeaveType lt WHERE lt.isActive = true")
    Long countActiveLeaveTypes();
}