package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Bonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BonusRepository extends JpaRepository<Bonus, Long> {

    // ✅ Employee ID (Long - primary key) দিয়ে Bonus খুঁজবেন
    List<Bonus> findByEmployeeId(Long employeeId);

    // ✅ Employee ID (String - business ID) দিয়ে Bonus খুঁজবেন
    @Query("SELECT b FROM Bonus b WHERE b.employee.employeeId = :employeeId")
    List<Bonus> findByEmployeeEmployeeId(@Param("employeeId") String employeeId);

    List<Bonus> findByBonusDateBetween(LocalDate startDate, LocalDate endDate);
    List<Bonus> findByStatus(Bonus.BonusStatus status);

    @Query("SELECT SUM(b.amount) FROM Bonus b WHERE b.bonusDate BETWEEN :startDate AND :endDate")
    Double getTotalBonusByDateRange(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Bonus b WHERE b.employee.department.id = :departmentId")
    List<Bonus> findByDepartmentId(Long departmentId);
}