
CREATE TABLE IF NOT EXISTS person
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    surname VARCHAR(255),
    pesel VARCHAR(20),
    height DOUBLE,
    weight DOUBLE,
    email_address VARCHAR(255),
    type VARCHAR(50), -- Type differentiates between Employee, Student, Pensioner, etc.
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
CREATE OR REPLACE VIEW person_view AS
SELECT
    p.id AS person_id,
    p.name,
    p.surname,
    p.pesel,
    p.height,
    p.weight,
    p.email_address,
    p.type,
    jp.position_name,
    jp.start_date,
    jp.end_date,
    jp.salary,
    COUNT(jp.id) OVER (PARTITION BY p.id) AS job_position_count
FROM person p
         LEFT JOIN job_position jp ON p.id = jp.employee_id
WHERE p.type = 'employee'
GROUP BY p.id, p.name, p.surname, p.pesel, p.height, p.weight, p.email_address, p.type,
         jp.position_name, jp.start_date, jp.end_date, jp.salary
HAVING COUNT(jp.id) > 0;

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

