INSERT INTO team (team_profile_id, created_at, modified_at, status)
SELECT
    tp.id,
    NOW(),
    NOW(),
    CASE WHEN tp.status = 'DELETED' THEN 'DELETED' ELSE 'USABLE' END
FROM team_profile tp;

INSERT INTO team_member (member_id, team_id)
SELECT tp.member_id, t.id
FROM team_profile tp
JOIN team t ON tp.id = t.team_profile_id
WHERE tp.member_id IS NOT NULL;
