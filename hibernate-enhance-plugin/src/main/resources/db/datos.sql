insert into Dependency (id, code) values (1, 'A-1');
insert into Dependency (id, code) values (2, 'AZ-2');
insert into Dependency (id, code) values (3, 'XD-3');
insert into Dependency (id, code) values (4, 'B-4');


insert into humanresource (id, username, name)  values (1, 'duke', 'The Java Duke');
insert into humanresource (id, username, name)  values (2, 'costax', 'Joao Costa');
insert into humanresource (id, username, name)  values (3, 'airjordan', 'M. Jordan');

INSERT INTO cc (id, rh_id, dependency_id, description) VALUES (1, 1, 1, 'Moviment 1');
INSERT INTO document (id, createdat, cc_id) VALUES (1, '2018-09-19 21:22:25.160000', 1);

INSERT INTO cc (id, rh_id, dependency_id, description) VALUES (2, 2, 1, 'Moviment 2');
INSERT INTO document (id, createdat, cc_id) VALUES (2, '2018-09-19 21:22:25.160000', 2);

INSERT INTO cc (id, rh_id, dependency_id, description) VALUES (3, 3, 3, 'Moviment 3');
INSERT INTO document (id, createdat, cc_id) VALUES (3, '2018-09-19 21:22:25.160000', 3);

INSERT INTO cc (id, rh_id, dependency_id, description) VALUES (4, 1, 3, 'Moviment 3');
INSERT INTO document (id, createdat, cc_id) VALUES (4, '2018-09-18 21:22:25.160000', 4);

INSERT INTO cc (id, rh_id, dependency_id, description) VALUES (5, 2, 4, 'Moviment 3');
INSERT INTO document (id, createdat, cc_id) VALUES (5, '2018-09-17 21:22:25.160000', 5);