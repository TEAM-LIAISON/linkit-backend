ALTER TABLE announcement_position
    DROP COLUMN created_at,
    DROP COLUMN modified_at,
    MODIFY COLUMN team_member_announcement_id BIGINT NOT NULL,
    MODIFY COLUMN position_id BIGINT NOT NULL;
