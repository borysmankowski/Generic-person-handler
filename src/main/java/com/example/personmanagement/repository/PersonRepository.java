package com.example.personmanagement.repository;

import com.example.personmanagement.model.person.Person;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    @EntityGraph(value = "Employee.jobPositions", type = EntityGraph.EntityGraphType.FETCH)
    Optional<Person> findById(Long id);
}