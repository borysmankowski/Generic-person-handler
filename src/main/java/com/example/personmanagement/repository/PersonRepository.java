package com.example.personmanagement.repository;

import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.person.Person;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    Optional<Person> findById(Long id);

    @Query("select e from Employee e left join fetch e.jobPositions where e.id=:id")
    Optional<Employee> findByIdWithJobs(@Param("id") Long id);

    @Query(value = "SELECT p.id AS id, p.name AS name, p.surname AS surname, p.job_position_count AS numberOfJobPositions FROM person_view p WHERE p.type = 'employee'", nativeQuery = true)
    List<EmployeeDto> findAllEmployeesWithJobPositions();
}