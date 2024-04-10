create table if not exists public.file_import
(
    id                 bigserial primary key,
    created_at         timestamp(6),
    file_path          varchar(255),
    finished_at        timestamp(6),
    last_processed_row bigint,
    started_at         timestamp(6),
    status             varchar(255)
    constraint file_import_status_check
    check ((status)::text = ANY
((ARRAY ['PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying])::text[]))
    );