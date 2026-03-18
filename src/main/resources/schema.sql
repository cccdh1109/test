CREATE TABLE IF NOT EXISTS import_job_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_code VARCHAR(64) NOT NULL UNIQUE,
    job_name VARCHAR(128) NOT NULL,
    cron_expression VARCHAR(64) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    oss_object_key VARCHAR(255) NOT NULL,
    file_suffix VARCHAR(16) NOT NULL,
    delimiter VARCHAR(8) NOT NULL,
    index_name VARCHAR(128) NOT NULL,
    field_mappings VARCHAR(1000) NOT NULL,
    handler_type VARCHAR(64) NOT NULL DEFAULT 'default',
    batch_size INT DEFAULT 500,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS import_job_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    job_id BIGINT NOT NULL,
    job_code VARCHAR(64) NOT NULL,
    trigger_type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,
    message VARCHAR(1000),
    total_lines INT DEFAULT 0,
    success_lines INT DEFAULT 0,
    failed_lines INT DEFAULT 0,
    duration_ms BIGINT DEFAULT 0,
    started_at DATETIME NOT NULL,
    finished_at DATETIME NULL,
    KEY idx_job_code(job_code)
);
