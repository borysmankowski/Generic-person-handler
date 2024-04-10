CREATE TABLE IF NOT EXISTS public.file_import (
                                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                  created_at TIMESTAMP(6),
    file_path VARCHAR(255),
    finished_at TIMESTAMP(6),
    last_processed_row BIGINT,
    started_at TIMESTAMP(6),
    status VARCHAR(255),
    CONSTRAINT file_import_status_check CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED'))
    );
