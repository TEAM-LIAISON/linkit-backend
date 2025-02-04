ALTER TABLE team_member_announcement
    MODIFY COLUMN main_tasks VARCHAR(10000) NOT NULL,
    MODIFY COLUMN work_method VARCHAR(10000) NOT NULL,
    MODIFY COLUMN ideal_candidate VARCHAR(10000) NOT NULL,
    MODIFY COLUMN preferred_qualifications VARCHAR(10000),
    MODIFY COLUMN joining_process VARCHAR(10000),
    MODIFY COLUMN benefits VARCHAR(10000);
