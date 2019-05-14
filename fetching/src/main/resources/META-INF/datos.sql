delete from issue where id > 0;

delete from client where id > 0;
delete from issue where id > 0;
delete from project where id > 0;

insert into project (id, version, title) values (1, 1, 'effective-java-3');
insert into project (id, version, title) values (2, 1, 'clean code');
insert into project (id, version, title) values (3, 1, 'example-1');
insert into project (id, version, title) values (4, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (5, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (6, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (7, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (8, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (9, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (10, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (11, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (12, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (13, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (14, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (15, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (16, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (17, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (18, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (19, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (20, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (21, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (22, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (23, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (24, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (25, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (26, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (27, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (28, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (29, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (30, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (31, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (32, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (33, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (34, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (35, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (36, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (37, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (38, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (39, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (40, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (41, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (42, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (43, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (44, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (45, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (46, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (47, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (48, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (49, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (50, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (51, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (52, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (53, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (54, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');
insert into project (id, version, title) values (55, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');

insert into issue (id, version, project_id, title, description, create_at) VALUES (1, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');
insert into issue (id, version, project_id, title, description, create_at) VALUES (2, 1, 1, 'effective-issue-2', 'effective-issue-1', '2018-01-01');
insert into issue (id, version, project_id, title, description, create_at) VALUES (3, 1, 1, 'effective-issue-3', 'effective-issue-1', '2018-01-01');
insert into issue (id, version, project_id, title, description, create_at) VALUES (4, 1, 1, 'effective-issue-4', 'effective-issue-1', '2018-01-01');
insert into issue (id, version, project_id, title, description, create_at) VALUES (5, 1, 1, 'effective-issue-5', 'effective-issue-1', '2018-01-01');

insert into issue (id, parent_id, version, project_id, title, description, create_at) VALUES (6, 1, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');
insert into issue (id, parent_id, version, project_id, title, description, create_at) VALUES (7, 6, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');
insert into issue (id, parent_id, version, project_id, title, description, create_at) VALUES (8, 6, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');
insert into issue (id, parent_id, version, project_id, title, description, create_at) VALUES (9, 7, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');
insert into issue (id, parent_id, version, project_id, title, description, create_at) VALUES (10, 7, 1, 1, 'effective-issue-1', 'effective-issue-1', '2018-01-01');


insert into client (id, name, slug) values (1, 'P.tech', 'ptech');
insert into client (id, name, slug) values (2, 'FeedIt', 'feedit');

