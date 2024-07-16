package com.example.personmanagement.repository;

import com.example.personmanagement.model.position.JobPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface JobPositionRepository extends JpaRepository<JobPosition, Long> {

    List<JobPosition> findByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long employeeId, LocalDate endDate, LocalDate startDate);

}
