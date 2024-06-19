package com.example.personmanagement.person.model;

import org.springframework.jdbc.core.JdbcTemplate;

public interface PersonFileImportStrategy {
    void insert(String[] data, JdbcTemplate jdbcTemplate);
}