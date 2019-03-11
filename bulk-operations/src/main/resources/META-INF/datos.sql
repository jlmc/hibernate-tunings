delete from issue where id > 0;
delete from project where id > 0;

insert into project (id, version, title) values (1, 1, 'effective-java-3');
insert into project (id, version, title) values (2, 1, 'clean code');
insert into project (id, version, title) values (3, 1, 'Enterprise_JavaBeans_3dot1_Sixth_Edition');

insert into issue (id, version, project_id, title, description, create_at) values (1, 1, 1, 'Item 1', 'Consider static factory methods instead of constructors', now());
insert into issue (id, version, project_id, title, description, create_at) values (2, 1, 1, 'Item 2', 'Consider a builder when faced with many constructor parameters', now());
insert into issue (id, version, project_id, title, description, create_at) values (3, 1, 1, 'Item 3', 'Enforce the singleton property with a private constructor or an enum type', now());
insert into issue (id, version, project_id, title, description, create_at) values (4, 1, 2, 'Meaningful Names', 'Use Intention-Revealing Names', now());
insert into issue (id, version, project_id, title, description, create_at) values (5, 1, 2, 'Functions', 'In the early days of programming we composed ...', now());
