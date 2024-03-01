package com.example.personmanagement.employee;

import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.person.model.Person;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Employee> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT e FROM Employee e WHERE e.id = :id")
    Optional<Person> findEmployeeWithLock(Long id);
}
