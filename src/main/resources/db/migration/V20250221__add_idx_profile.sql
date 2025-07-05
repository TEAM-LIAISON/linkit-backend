CREATE INDEX idx_profile_status_public ON profile(status, is_profile_public);
CREATE INDEX idx_profile_created_at ON profile (created_at);