insert into permission (id, name) values (1, 'CREATE_BLOG');
insert into permission (id, name) values (2, 'READ');
insert into permission (id, name) values (3, 'WRITE');

insert into user (id, name) values (1001, 'Joana');
insert into user (id, name) values (1002, 'Rodrigo');

insert into user_permission(user_id, permission_id) values (1001, 1);
insert into user_permission(user_id, permission_id) values (1001, 2);
insert into user_permission(user_id, permission_id) values (1001, 3);
insert into user_permission(user_id, permission_id) values (1002, 2);
