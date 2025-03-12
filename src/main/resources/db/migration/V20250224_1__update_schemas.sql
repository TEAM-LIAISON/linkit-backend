
ALTER TABLE announcement_scrap
	MODIFY COLUMN member_id BIGINT NOT NULL,
    MODIFY COLUMN team_member_announcement_id BIGINT NOT NULL;


ALTER TABLE announcement_skill
	DROP COLUMN created_at,
    DROP COLUMN modified_at,
	MODIFY COLUMN skill_id BIGINT NOT NULL,
    MODIFY COLUMN team_member_announcement_id BIGINT NOT NULL,
    RENAME COLUMN announcement_skill_id TO id;

ALTER TABLE chat_room
    RENAME COLUMN participantamember_id TO participant_a_member_id,
    RENAME COLUMN participantaid TO participant_a_id,
    RENAME COLUMN participantaname TO participant_a_name;

ALTER TABLE member_basic_inform
	RENAME COLUMN member_basic_inform_id TO id,
    MODIFY COLUMN member_id BIGINT NOT NULL;

ALTER TABLe profile_education
	MODIFY COLUMN profile_id BIGINT NOT NULL,
    MODIFY COLUMN university_id BIGINT NOT NULL;

ALTER TABLE team_member_announcement
	RENAME COLUMN team_member_announcement_id TO id,
    MODIFY COLUMN team_id BIGINT NOT NULL;

ALTER TABLE team_scrap
	MODIFY COLUMN team_id BIGINT NOT NULL,
    MODIFY COLUMN member_id BIGINT NOT NULl;
