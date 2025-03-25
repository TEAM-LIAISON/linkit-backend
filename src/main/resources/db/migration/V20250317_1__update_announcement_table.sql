CREATE TABLE work_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    work_type_name VARCHAR(255) NOT NULL
);

-- ProjectType 테이블 생성
CREATE TABLE project_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_type_name VARCHAR(255) NOT NULL
);

CREATE TABLE announcement_work_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_member_announcement_id BIGINT NOT NULL,
    work_type_id BIGINT NOT NULL,
    FOREIGN KEY (team_member_announcement_id) REFERENCES team_member_announcement(id),
    FOREIGN KEY (work_type_id) REFERENCES work_type(id)
);

CREATE TABLE announcement_project_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    team_member_announcement_id BIGINT NOT NULL,
    project_type_id BIGINT NOT NULL,
    FOREIGN KEY (team_member_announcement_id) REFERENCES team_member_announcement(id),
    FOREIGN KEY (project_type_id) REFERENCES project_type(id)
);
