-- TeamLog 테이블에 댓글 수(comment_count) 필드 추가
ALTER TABLE team_log ADD COLUMN comment_count BIGINT NOT NULL DEFAULT 0;

-- 기존 데이터에 대한 comment_count 초기화
-- (실제 댓글 수를 계산할 수 있다면 여기서 실제 값으로 설정 가능)
UPDATE team_log SET comment_count = 0;
