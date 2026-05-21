package com.example.personmanagement.search;

import com.example.personmanagement.model.person.Person;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;


@Component
public class JobPositionsSpecificationProvider implements SpecificationProvider {

    @Override
    public boolean supports(SearchCriteria criteria) {
        return "numberOfJobPositions".equals(criteria.getKey())
                && "range".equals(criteria.getOperation());
    }

    @Override
    public Specification<Person> getSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {

            Integer min = parseInteger(criteria.getValue());
            Integer max = parseInteger(criteria.getSecondValue());

            Predicate isEmployee = criteriaBuilder.equal(root.get("type"), "EMPLOYEE");

            Subquery<Long> jobCountSubquery = query.subquery(Long.class);
            Root<Person> subRoot = jobCountSubquery.correlate(root);
            Join<Person, ?> jobsJoin = subRoot.join("jobPositions", JoinType.LEFT);
            jobCountSubquery.select(criteriaBuilder.count(jobsJoin.get("id")));

            query.distinct(true);

            Expression<Long> jobCount = jobCountSubquery;
            Predicate countPredicate;

            if (min != null && max != null) {
                countPredicate = criteriaBuilder.between(jobCount, (long) min, (long) max);
            } else if (min != null) {
                countPredicate = criteriaBuilder.greaterThanOrEqualTo(jobCount, (long) min);
            } else if (max != null) {
                countPredicate = criteriaBuilder.lessThanOrEqualTo(jobCount, (long) max);
            } else {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(isEmployee, countPredicate);
        };
    }

    private Integer parseInteger(Object value) {
        if (value instanceof String s) {
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { return null; }
        }
        return value instanceof Integer i ? i : null;
    }
}