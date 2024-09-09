package com.example.personmanagement.search;

import com.example.personmanagement.model.employee.EmployeeDto;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.repository.PersonRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class JobPositionsSpecificationProvider implements SpecificationProvider {

    private final PersonRepository personRepository;

    public JobPositionsSpecificationProvider(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public boolean supports(SearchCriteria criteria) {
         return "numberOfJobPositions".equals(criteria.getKey()) && "range".equals(criteria.getOperation());
    }

    @Override
    public Specification<Person> getSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {

            List<EmployeeDto> employees = personRepository.findAllEmployeesWithJobPositions();

            Integer minJobPositions = parseInteger(criteria.getValue());
            Integer maxJobPositions = parseInteger(criteria.getSecondValue());

            return buildJobPositionPredicate(minJobPositions, maxJobPositions, root, criteriaBuilder);
        };
    }

    private Predicate buildJobPositionPredicate(Integer minJobPositions, Integer maxJobPositions, Root<Person> root, CriteriaBuilder criteriaBuilder) {
        Expression<Integer> jobPositionCount = root.get("numberOfJobPositions");

        if (minJobPositions != null && maxJobPositions != null) {
            return criteriaBuilder.between(jobPositionCount, minJobPositions, maxJobPositions);
        } else if (minJobPositions != null) {
            return criteriaBuilder.greaterThanOrEqualTo(jobPositionCount, minJobPositions);
        } else if (maxJobPositions != null) {
            return criteriaBuilder.lessThanOrEqualTo(jobPositionCount, maxJobPositions);
        }

        return criteriaBuilder.conjunction();
    }

    private Integer parseInteger(Object value) {
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        } else if (value instanceof Integer) {
            return (Integer) value;
        }
        return null;
    }
}