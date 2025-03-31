-- 프로필 로그 댓글 테이블 생성
CREATE TABLE profile_log_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_log_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    parent_comment_id BIGINT,
    content VARCHAR(1000) NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,

    -- 외래 키 제약조건
    CONSTRAINT fk_profile_log_comment_profile_log
        FOREIGN KEY (profile_log_id)
        REFERENCES profile_log (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_profile_log_comment_profile
        FOREIGN KEY (profile_id)
        REFERENCES profile (id),

    CONSTRAINT fk_profile_log_comment_parent
        FOREIGN KEY (parent_comment_id)
        REFERENCES profile_log_comment (id)
        ON DELETE CASCADE
);

-- 인덱스 생성
CREATE INDEX idx_profile_log_comment_profile_log_id ON profile_log_comment (profile_log_id);
CREATE INDEX idx_profile_log_comment_profile_id ON profile_log_comment (profile_id);
CREATE INDEX idx_profile_log_comment_parent_id ON profile_log_comment (parent_comment_id);
CREATE INDEX idx_profile_log_comment_created_at ON profile_log_comment (created_at);
