package com.example.personmanagement.search;

import com.example.personmanagement.model.person.Person;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class PersonSpecification {

    private final List<SpecificationProvider> specificationProviders;

    public PersonSpecification(List<SpecificationProvider> specificationProviders) {
        this.specificationProviders = specificationProviders;
    }

    public static Specification<Person> any() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
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

    public Specification<Person> addSpecification(Specification<Person> specification, SearchCriteria criteria) {
        for (SpecificationProvider provider : specificationProviders) {
            if (provider.supports(criteria)) {
                return specification.and(provider.getSpecification(criteria));
            }
        }

        switch (criteria.getOperation()) {
            case "eq" -> specification = specification.and(equalSpecification(criteria));
            case "like" -> specification = specification.and(likeSpecification(criteria));
            case "range" -> specification = specification.and(rangeSpecification(criteria));
            default -> {
            }
        }
        return specification;
    }

    private Specification<Person> equalSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
    }

    private Specification<Person> likeSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get(criteria.getKey())),
                        "%" + criteria.getValue().toString().toLowerCase() + "%");
    }

    private <T extends Comparable<? super T>> Specification<Person> rangeSpecification(SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            String key = criteria.getKey();
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
                    return criteriaBuilder.between(root.get(key), (T) minDate, (T) maxDate);
                } else {
                    return null;
                }
            } else {
                if (minValue != null) {
                    if (maxValue != null) {
                        return criteriaBuilder.between(root.get(key), (T) minValue, (T) maxValue);
                    } else {
                        return criteriaBuilder.greaterThanOrEqualTo(root.get(key), (T) minValue);
                    }
                } else {
                    return null;
                }
            }
        };
    }
}