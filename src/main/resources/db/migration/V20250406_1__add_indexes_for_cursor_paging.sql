-- Member.emailId 조회 성능 개선
CREATE INDEX idx_member_email_id ON member (email_id);
CREATE INDEX idx_profile_status_public_id ON profile (status, is_profile_public, id DESC);
CREATE INDEX idx_profile_created_at ON profile (created_at DESC);
CREATE INDEX idx_position_sub_position ON position (sub_position);
CREATE INDEX idx_region_city_name ON region (city_name);
CREATE INDEX idx_profile_state_name ON profile_state (profile_state_name);
