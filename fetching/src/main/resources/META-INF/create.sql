delete from issue where id > 0;
delete from project where id > 0;

ALTER SEQUENCE project_id_seq RESTART WITH 56;
ALTER SEQUENCE issue_id_seq RESTART WITH 11;