ALTER TABLE product_sub_image
	RENAME COLUMN product_portfolio_id TO team_product_id;

ALTER TABLE profile
	MODIFY member_id BIGINT NOT NULL;

ALTER TABLE profile_activity
    MODIFY activity_start_date VARCHAR(255) NOT NULL,
    MODIFY profile_id BIGINT NOT NULL;

ALTER TABLE profile_awards
    MODIFY profile_id BIGINT NOT NULL;

ALTER TABLE profile_current_state
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY profile_id BIGINT NOT NULL,
    MODIFY profile_state_id BIGINT NOT NULL;

ALTER TABLE profile_log
    MODIFY profile_id BIGINT NOT NULL;

ALTER TABLE profile_log_image
	MODIFY profile_log_id BIGINT NOT NULL,
    MODIFY image_id BIGINT NOT NULL;

ALTER TABLE profile_portfolio
    MODIFY profile_id BIGINT NOT NULL;
