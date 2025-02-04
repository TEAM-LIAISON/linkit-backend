-- V1.0.0__Update_Announcement_Length.sql

ALTER TABLE team_member_announcement
    MODIFY COLUMN mainTasks VARCHAR(10000) NOT NULL,
    MODIFY COLUMN workMethod VARCHAR(10000) NOT NULL,
    MODIFY COLUMN idealCandidate VARCHAR(10000) NOT NULL,
    MODIFY COLUMN preferredQualifications VARCHAR(10000),
    MODIFY COLUMN joiningProcess VARCHAR(10000),
    MODIFY COLUMN benefits VARCHAR(10000);
