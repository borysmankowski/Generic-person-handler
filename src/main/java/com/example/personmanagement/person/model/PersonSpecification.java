package com.example.personmanagement.person.model;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PersonSpecification {

    public static Specification<Person> any() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }


    public Specification<Person> addSpecification(Specification<Person> specification, SearchCriteria criteria) {
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
            Object minValue = criteria.getValue();
            Object maxValue = criteria.getSecondValue();

            if (minValue != null) {
                if (maxValue != null) {
                    return criteriaBuilder.between(root.get(criteria.getKey()), (T) minValue, (T) maxValue);
                } else {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()), (T) minValue);
                }
            } else {
                return null;
            }
        };
    }
}