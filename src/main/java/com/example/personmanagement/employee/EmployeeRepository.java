package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e left join fetch e.jobPositions jb where e.id=:id")
    Optional<Employee> findById(@Param("id") Long id);
}