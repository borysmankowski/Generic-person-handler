package com.example.personmanagement.person.model;

import org.springframework.data.jpa.domain.Specification;

public class PersonSpecification {

    public static Specification<Person> any() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }

    public static Specification<Person> typeEquals(String type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Person> nameContainsIgnoreCase(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Person> surnameContainsIgnoreCase(String surname) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")), "%" + surname.toLowerCase() + "%");
    }

    public static Specification<Person> ageBetween(Integer ageFrom, Integer ageTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("age"), ageFrom, ageTo);
    }

    public static Specification<Person> peselEquals(String pesel) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("pesel"), pesel);
    }

    public static Specification<Person> genderEquals(String gender) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("gender"), gender);
    }

    public static Specification<Person> heightBetween(Double heightFrom, Double heightTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("height"), heightFrom, heightTo);
    }

    public static Specification<Person> weightBetween(Double weightFrom, Double weightTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("weight"), weightFrom, weightTo);
    }

    public static Specification<Person> emailAddressContainsIgnoreCase(String emailAddress) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("emailAddress")), "%" + emailAddress.toLowerCase() + "%");
    }

    public static Specification<Person> salaryBetween(Double salaryFrom, Double salaryTo) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("currentSalary"), salaryFrom, salaryTo);
    }

    public static Specification<Person> universityNameEquals(String universityName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("nameOfUniversity"), universityName);
    }

    public static Specification<Person> numberOfJobPositionsBetween(Integer numberOfJobPositionsFrom, Integer numberOfJobPositionsTo) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("numberOfJobPositions"), numberOfJobPositionsFrom, numberOfJobPositionsTo));
    }
}