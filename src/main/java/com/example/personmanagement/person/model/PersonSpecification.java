package com.example.personmanagement.person.model;

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
            default -> {
            }
        }
        return specification;
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