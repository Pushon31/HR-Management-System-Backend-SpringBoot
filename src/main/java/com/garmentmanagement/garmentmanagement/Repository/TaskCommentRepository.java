// Repository: TaskCommentRepository.java
package com.garmentmanagement.garmentmanagement.Repository;

import com.garmentmanagement.garmentmanagement.Entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {
    List<TaskComment> findByTaskIdOrderByCommentDateDesc(Long taskId);
    List<TaskComment> findByEmployeeId(Long employeeId);
    Long countByTaskId(Long taskId);
}