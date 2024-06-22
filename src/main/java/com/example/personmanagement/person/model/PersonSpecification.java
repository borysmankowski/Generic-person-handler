package com.example.personmanagement.person.model;

import com.example.personmanagement.employee.model.Employee;
import com.example.personmanagement.employee.position.JobPosition;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Component
public class PersonSpecification {

    public static Specification<Person> any() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }


    public static Specification<Person> addSpecification(Specification<Person> specification, SearchCriteria criteria) {
        switch (criteria.getOperation()) {
            case "eq" -> specification = specification.and(equalSpecification(criteria));
            case "like" -> specification = specification.and(likeSpecification(criteria));
            case "range" -> specification = specification.and(rangeSpecification(criteria));
            case "salaryRange" -> specification = specification.and(salaryRangeSpecification(criteria));
            default -> {
            }
        }
        return specification;
    }

    private static Specification<Person> salaryRangeSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            // Ensure this specification is applied only to Employee entities
            if (!Employee.class.isAssignableFrom(root.getJavaType())) {
                return criteriaBuilder.conjunction();
            }

            // Extract minSalary and maxSalary from the criteria
            Double minSalary = (Double) criteria.getValue();
            Double maxSalary = (Double) criteria.getSecondValue();

            // Create a subquery to fetch the job positions for each employee
            Subquery<Double> subquery = query.subquery(Double.class);
            Root<JobPosition> subRoot = subquery.from(JobPosition.class);

            // Select the average salary for the job positions associated with each employee
            subquery.select(criteriaBuilder.avg(subRoot.get("salary")))
                    .where(criteriaBuilder.equal(subRoot.get("employee"), root));

            // Create predicates for the salary range
            Predicate salaryPredicate = null;
            if (minSalary != null && maxSalary != null) {
                salaryPredicate = criteriaBuilder.between(subquery, minSalary, maxSalary);
            } else if (minSalary != null) {
                salaryPredicate = criteriaBuilder.greaterThanOrEqualTo(subquery, minSalary);
            } else if (maxSalary != null) {
                salaryPredicate = criteriaBuilder.lessThanOrEqualTo(subquery, maxSalary);
            }

            // Ensure that employees without job positions are excluded
            Predicate hasJobPositionPredicate = criteriaBuilder.exists(subquery);

            // Combine the salary predicate and the hasJobPositionPredicate
            if (salaryPredicate != null) {
                return criteriaBuilder.and(hasJobPositionPredicate, salaryPredicate);
            } else {
                return hasJobPositionPredicate;
            }
        };
    }

    private static Specification<Person> equalSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
    }

    private static Specification<Person> likeSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(criteria.getKey())),
                        "%" + criteria.getValue().toString().toLowerCase() + "%");
    }

    private static <T extends Comparable<? super T>> Specification<Person> rangeSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            Object minValue = criteria.getValue();
            Object maxValue = criteria.getSecondValue();

            if (isDate(minValue) && isDate(maxValue)) {
                LocalDate minDate = parseDate(minValue);
                LocalDate maxDate = parseDate(maxValue);

                if (minDate != null && maxDate != null) {
                    return criteriaBuilder.between(root.get(criteria.getKey()), (T) minDate, (T) maxDate);
                } else {
                    return null;
                }
            } else {
                if (minValue != null) {
                    if (maxValue != null) {
                        return criteriaBuilder.between(root.get(criteria.getKey()), (T) minValue, (T) maxValue);
                    } else {
                        return criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), (T) minValue);
                    }
                } else {
                    return null;
                }
            }
        };
    }

    private static LocalDate parseDate(Object value) {
        try {
            return LocalDate.parse((String) value);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static boolean isDate(Object value) {
        return parseDate(value) != null;
    }
}