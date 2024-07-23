package com.example.personmanagement.search;

import com.example.personmanagement.model.person.Person;
import org.springframework.data.jpa.domain.Specification;

public interface SpecificationProvider {

    boolean supports(SearchCriteria criteria);
    Specification<Person> getSpecification(SearchCriteria criteria);
}
