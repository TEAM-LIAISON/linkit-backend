CREATE TABLE team (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_profile_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    modified_at DATETIME(6) NOT NULL,
    status enum ('USABLE', 'DELETED') NOT NULL,
    FOREIGN KEY (team_profile_id) REFERENCES team_profile(id)
);

CREATE TABLE team_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    team_id BIGINT NOT NULL,
    FOREIGN KEY (member_id) REFERENCES member(id),
    FOREIGN KEY (team_id) REFERENCES team(id)
);
