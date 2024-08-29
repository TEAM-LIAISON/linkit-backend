ALTER TABLE private_matching
RENAME COLUMN receiver_delete_status_type TO success_receiver_delete_status_type;

ALTER TABLE private_matching
MODIFY COLUMN success_receiver_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';

ALTER TABLE private_matching
ADD COLUMN success_sender_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';

ALTER TABLE private_matching
ADD COLUMN request_sender_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';

ALTER TABLE team_matching
RENAME COLUMN receiver_delete_status_type TO success_receiver_delete_status_type;

ALTER TABLE team_matching
MODIFY COLUMN success_receiver_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';

ALTER TABLE team_matching
ADD COLUMN success_sender_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';

ALTER TABLE team_matching
ADD COLUMN request_sender_delete_status_type ENUM('DELETED', 'REMAINED') NOT NULL DEFAULT 'REMAINED';
