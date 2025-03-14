-- Flyway migration script to create profile_visits table

CREATE TABLE profile_visits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    visited_profile_id BIGINT NOT NULL,
    visit_time DATETIME NOT NULL,

    -- Foreign key constraint for visitor profile
    CONSTRAINT fk_profile_visits_visitor_profile
    FOREIGN KEY (profile_id)
    REFERENCES profile(id),

    -- Index on visited_profile_id for faster queries
    INDEX idx_profile_visits_visited (visited_profile_id),

    -- Index on visit_time for sorting by most recent
    INDEX idx_profile_visits_time (visit_time),

    -- Composite index for common query patterns (finding visits to a profile in time order)
    INDEX idx_profile_visits_profile_time (visited_profile_id, visit_time DESC)
);
