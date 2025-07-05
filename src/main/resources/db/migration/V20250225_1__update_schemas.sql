ALTER TABLE profile_position
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY COLUMN profile_id BIGINT NOT NULL,
    MODIFY COLUMN position_id BIGINT NOT NULL;

ALTER TABLE profile_region
    MODIFY COLUMN profile_id BIGINT NOT NULL,
    MODIFY COLUMN region_id BIGINT NOT NULL;

ALTER TABLE profile_scrap
	MODIFY COLUMN member_id BIGINT NOT NULL,
    MODIFY COLUMN profile_id BIGINT NOT NULL;

ALTER TABLE profile_skill
	MODIFY COLUMN profile_id BIGINT NOT NULL,
    MODIFY COLUMN skill_id BIGINT NOT NULL,
    MODIFY COLUMN skill_level VARCHAR(255) NOT NULL;

ALTER TABLE project_link
	MODIFY COLUMN project_link_name VARCHAR(5) NOT NULL,
    MODIFY COLUMN profile_portfolio_id BIGINT NOT NULL,
    MODIFY COLUMN project_link_url VARCHAR(255) NOT NULL;

ALTER TABLE project_role_contribution
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY COLUMN profile_portfolio_id BIGINT NOT NULL;

ALTER TABLE project_skill
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY COLUMN profile_portfolio_id BIGINT NOT NULL,
    MODIFY COLUMN skill_id BIGINT NOT NULL;

ALTER TABLE project_sub_image
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY COLUMN profile_portfolio_id BIGINT NOT NULL,
    MODIFY COLUMN project_sub_image_path VARCHAR(255) NOT NULL;

ALTER TABLE region
	MODIFY COLUMN city_name VARCHAR(255) NOT NULL,
    MODIFY COLUMN division_name VARCHAR(255) NOT NULL,
    RENAME COLUMN region_id TO id;

ALTER TABLE scale
	MODIFY COLUMN scale_name VARCHAR(255) NOT NULL;

ALTER TABLE skill
	MODIFY COLUMN skill_name VARCHAR(255) NOT NULL,
    RENAME COLUMN skill_id TO id;
