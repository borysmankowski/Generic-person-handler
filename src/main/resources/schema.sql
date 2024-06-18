CREATE TABLE IF NOT EXISTS file_import (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           created_at TIMESTAMP(6),
    file_path VARCHAR(255),
    finished_at TIMESTAMP(6),
    last_processed_row BIGINT,
    started_at TIMESTAMP(6),
    status VARCHAR(255),
    CONSTRAINT file_import_status_check CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'IN_PROGRESS'))
    );

CREATE TABLE IF NOT EXISTS student (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       type VARCHAR(255),
    name VARCHAR(255),
    surname VARCHAR(255),
    pesel VARCHAR(11),
    height DOUBLE,
    weight DOUBLE,
    email_address VARCHAR(255),
    name_of_university VARCHAR(255),
    year_of_studies INT,
    course_name VARCHAR(255),
    scholarship DOUBLE
    );

CREATE TABLE IF NOT EXISTS pensioner (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         type VARCHAR(255),
    name VARCHAR(255),
    surname VARCHAR(255),
    pesel VARCHAR(11),
    height DOUBLE,
    weight DOUBLE,
    email_address VARCHAR(255),
    pension_amount DOUBLE,
    worked_years INT
    );

CREATE TABLE IF NOT EXISTS employee (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        type VARCHAR(255),
    name VARCHAR(255),
    surname VARCHAR(255),
    pesel VARCHAR(11),
    height DOUBLE,
    weight DOUBLE,
    email_address VARCHAR(255),
    employment_start_date DATE,
    current_position VARCHAR(255),
    current_salary DOUBLE
    );
