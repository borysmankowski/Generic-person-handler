package com.example.personmanagement.person.model;

import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface PersonFileImportStrategy {
    void insert(String[] data, JdbcTemplate jdbcTemplate);

    void bulkInsert(List<String[]> dataList, JdbcTemplate jdbcTemplate);
}