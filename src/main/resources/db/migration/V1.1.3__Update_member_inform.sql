ALTER TABLE member DROP COLUMN exist_default_private_profile;
ALTER TABLE member DROP COLUMN exist_default_team_profile;
ALTER TABLE member DROP COLUMN member_type;
ALTER TABLE member
RENAME COLUMN status TO member_state;

ALTER TABLE member
MODIFY COLUMN member_state ENUM('DORMANT', 'ACTIVE', 'DELETED') NOT NULL;

ALTER TABLE member_basic_inform
MODIFY COLUMN marketing_agree BIT(1) NOT NULL;

ALTER TABLE member_basic_inform
ADD COLUMN service_use_agree BIT(1) NOT NULL,
ADD COLUMN private_inform_agree BIT(1) NOT NULL,
ADD COLUMN age_check BIT(1) NOT NULL;
