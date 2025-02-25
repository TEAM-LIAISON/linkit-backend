ALTER TABLE profile_awards
    MODIFY COLUMN awards_date VARCHAR(255) NOT NULL;

ALTER TABLE profile_license
    MODIFY COLUMN profile_id BIGINT NOT NULL;

ALTER TABLE profile_portfolio
    MODIFY COLUMN project_start_date VARCHAR(255) NOT NULL;

ALTER TABLE profile_region
    RENAME COLUMN profile_region_id TO id;

ALTER TABLE team_current_state
    RENAME COLUMN profile_id TO team_id;
