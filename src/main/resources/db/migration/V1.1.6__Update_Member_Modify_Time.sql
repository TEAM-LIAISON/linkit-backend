ALTER TABLE member
RENAME COLUMN modified_at TO updated_at;

ALTER TABLE private_wish DROP COLUMN wish_type;
ALTER TABLE team_wish DROP COLUMN wish_type;
