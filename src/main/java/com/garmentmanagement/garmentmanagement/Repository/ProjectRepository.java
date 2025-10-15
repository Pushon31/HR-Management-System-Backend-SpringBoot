// Repository: ProjectRepository.java
package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByCode(String code);
    List<Project> findByStatus(Project.ProjectStatus status);
    List<Project> findByDepartmentId(Long departmentId);
    List<Project> findByProjectManagerId(Long projectManagerId);

    @Query("SELECT p FROM Project p WHERE p.projectManager.id = :managerId AND p.status = 'IN_PROGRESS'")
    List<Project> findActiveProjectsByManager(@Param("managerId") Long managerId);

    boolean existsByCode(String code);

    @Query("SELECT COUNT(p) FROM Project p WHERE p.status = 'IN_PROGRESS'")
    Long countActiveProjects();

    @Query("SELECT p.status, COUNT(p) FROM Project p GROUP BY p.status")
    List<Object[]> countProjectsByStatus();
}