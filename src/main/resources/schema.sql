CREATE TABLE IF NOT EXISTS person
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    pesel VARCHAR(20),
    height DOUBLE,
    weight DOUBLE,
    email_address VARCHAR(255),
    type VARCHAR(50),
    version INT
    );

CREATE TABLE IF NOT EXISTS job_position
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    position_name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    salary DOUBLE,
    employee_id BIGINT,
    FOREIGN KEY (employee_id) REFERENCES employee(id)
    );

CREATE TABLE IF NOT EXISTS file_import
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP(6),
    file_path VARCHAR(255),
    finished_at TIMESTAMP(6),
    last_processed_row BIGINT,
    started_at TIMESTAMP(6),
    status VARCHAR(255),
    CONSTRAINT file_import_status_check CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED', 'IN_PROGRESS'))
    );