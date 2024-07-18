package com.example.personmanagement.search;

import com.example.personmanagement.model.employee.Employee;
import com.example.personmanagement.model.person.Person;
import com.example.personmanagement.model.position.JobPosition;
import jakarta.persistence.criteria.Join;
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
            case "salaryRange" -> specification = specification.and(employeeSalaryBetween(criteria));
            default -> {
            }
        }
        return specification;
    }

    public static Specification<Person> employeeSalaryBetween(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            Double minValue = Double.parseDouble(criteria.getValue().toString());
            Double maxValue = Double.parseDouble(criteria.getSecondValue().toString());

            if (minValue > maxValue) {
                throw new IllegalArgumentException("Min value cannot be greater than max value");
            }

            Subquery<JobPosition> subquery = query.subquery(JobPosition.class);
            Root<Employee> employeeRoot = subquery.from(Employee.class);
            Join<Employee, JobPosition> jobPositionJoin = employeeRoot.join("jobPositions");

            subquery.select(jobPositionJoin.get("salary"))
                    .where(criteriaBuilder.equal(jobPositionJoin.get("employee"), root));

            Predicate salaryPredicate = criteriaBuilder.between(jobPositionJoin.get("salary"), minValue, maxValue);

            return criteriaBuilder.exists(subquery.select(jobPositionJoin).where(salaryPredicate));
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

            if (minValue instanceof Comparable && maxValue instanceof Comparable) {
                if (((Comparable) minValue).compareTo(maxValue) > 0) {
                    throw new IllegalArgumentException("Min value cannot be greater than max value");
                }
            }

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