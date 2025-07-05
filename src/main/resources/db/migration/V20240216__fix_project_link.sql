CREATE TABLE project_link (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_portfolio_id BIGINT NOT NULL,
    project_link_name VARCHAR(5),
    project_link_url VARCHAR(255),

    FOREIGN KEY (profile_portfolio_id) REFERENCES profile_portfolio(id) ON DELETE CASCADE
);

CREATE INDEX idx_project_link_profile_portfolio ON project_link(profile_portfolio_id);

INSERT INTO project_link (profile_portfolio_id, project_link_name, project_link_url)
SELECT id, '링크 제목', project_link FROM profile_portfolio
WHERE project_link IS NOT NULL;

ALTER TABLE profile_portfolio
    DROP COLUMN project_link;