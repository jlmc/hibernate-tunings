INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (1, 'Joshua', 'Bloch', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (2, 'Gavin', 'King', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (3, 'Christian', 'Bauer', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (4, 'Gary', 'Gregory', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (5, 'Raoul-Gabriel', 'Urma', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (6, 'Mario', 'Fusco', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (7, 'Alan', 'Mycroft', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (8, 'Andrew Lee', 'Rubinger', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (9, 'Aslak', 'Knutsen', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (10, 'Bill', 'Burke', 0);
INSERT INTO multimedia.actor (id, firstname, lastname, version)
VALUES (11, 'Scott', 'Oaks', 0);


INSERT INTO multimedia.serie (id, description, title, version)
VALUES (1, '2008-05-08', 'Effective Java', 0);
INSERT INTO multimedia.serie (id, description, title, version)
VALUES (2, '2015-10-01', 'Java Persistence with Hibernate', 0);
INSERT INTO multimedia.serie (id, description, title, version)
VALUES (3, '2014-08-28', 'Java 8 in Action', 0);
INSERT INTO multimedia.serie (id, description, title, version)
VALUES (4, '2014-03-12', 'Continuous Enterprise Development in Java', 0);
INSERT INTO multimedia.serie (id, description, title, version)
VALUES (5, '2010-09-08', 'Enterprise JavaBeans 3.1', 0);
INSERT INTO multimedia.serie (id, description, title, version)
VALUES (6, '2014-04-29', 'Java Performance The Definitive Guide', 0);


INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (1, 1);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (2, 2);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (2, 3);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (2, 4);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (3, 5);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (3, 6);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (3, 7);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (4, 8);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (4, 9);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (5, 8);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (5, 10);
INSERT INTO multimedia.serie_actor (serie_id, actor_id)
VALUES (6, 11);