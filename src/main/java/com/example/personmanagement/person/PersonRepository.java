package com.example.personmanagement.person;

import com.example.personmanagement.person.model.Person;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT p FROM Person p WHERE p.id = :id")
    Optional<Person> findPersonByIdWithLock(Long id);

    boolean existsByPesel(String pesel);

}


