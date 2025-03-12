CREATE TABLE team_visits (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    visited_team_id BIGINT NOT NULL,
    visit_time DATETIME NOT NULL,

    CONSTRAINT fk_profile_visits_visitor_team
    FOREIGN KEY (profile_id)
    REFERENCES profile(id),

    -- Index on visited_profile_id for faster queries
    INDEX idx_profile_visits_visited (visited_team_id),

    -- Index on visit_time for sorting by most recent
    INDEX idx_profile_visits_time (visit_time),

    -- Composite index for common query patterns (finding visits to a profile in time order)
    INDEX idx_profile_visits_team_time (visited_team_id, visit_time DESC)
);
