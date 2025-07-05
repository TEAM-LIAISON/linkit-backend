CREATE TABLE log_daily_view_count (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_view_type ENUM('PROFILE_LOG', 'TEAM_LOG') NOT NULL,
    log_id BIGINT NOT NULL,
    date DATE NOT NULL,
    daily_view_count BIGINT NOT NULL,
    CONSTRAINT uc_log_type_log_id_date UNIQUE (log_view_type, log_id, date)
);
