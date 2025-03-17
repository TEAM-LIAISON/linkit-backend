ALTER TABLE team_member_announcement
ADD COLUMN is_legacy_announcement BOOLEAN DEFAULT FALSE;

UPDATE team_member_announcement
SET is_legacy_announcement = TRUE
WHERE id > 0;

CREATE INDEX idx_team_member_announcement_legacy
ON team_member_announcement(is_legacy_announcement);
