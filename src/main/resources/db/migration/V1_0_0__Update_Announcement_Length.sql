ALTER TABLE team_member_announcement
    MODIFY COLUMN main_tasks TEXT NOT NULL,
    MODIFY COLUMN work_method TEXT NOT NULL,
    MODIFY COLUMN ideal_candidate TEXT NOT NULL,
    MODIFY COLUMN preferred_qualifications TEXT,
    MODIFY COLUMN joining_process TEXT,
    MODIFY COLUMN benefits TEXT;
