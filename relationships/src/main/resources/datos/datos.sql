-- ==============================
-- Directors
-- ==============================
insert into director (id, name) values (101, 'Emir Kusturica');
insert into director (id, name) values (102, 'Clint Eastwood');

-- ==============================
-- Movies
-- ==============================
insert into movie (id, title, director_id) values (1111, 'Maradona by Kusturica', 101);

-- ==============================
-- Schenes
-- ==============================
-- x sequence scene_seq start with 1 increment by 10
-- select scene_seq from dual;
-- call next value for scene_seq;
-- select scene_seq from dual;
insert into scene (movie_id, id, li, description) values (1111, 9995, 1, 'Sex Pistols');
insert into scene (movie_id, id, li, description) values (1111, 9992, 2, 'Manu Chao');
insert into scene (movie_id, id, li, description) values (1111, 9993, 3, 'La Mano de Dios');
insert into scene (movie_id, id, li, description) values (1111, 9994, 4, 'Para Siempre Diego');
insert into scene (movie_id, id, li, description) values (1111, 9991, 5, 'References---');


-- ==============================
-- Tv Ch
-- ==============================

INSERT INTO TVCHANNEL (ID, NAME) VALUES ('EUROSPORT1', 'Eurosport 1');

INSERT INTO TVPROGRAM (ID, CONTENT, END, START) VALUES (9991, 'South Africa x Namibia', '14:15:00', '15:20:00');
INSERT INTO TVPROGRAM (ID, CONTENT, END, START) VALUES (9992, 'Nauritania x Angola', '15:22:00', '17:40:00');
INSERT INTO TVPROGRAM (ID, CONTENT, END, START) VALUES (9993, 'Whatt', '17:50:00', '19:59:00');
INSERT INTO TVPROGRAM (ID, CONTENT, END, START) VALUES (9994, 'News', '20:00:00', '23:59:00');


INSERT INTO TVCHANNEL_TVPROGRAM (TVCHANNEL_ID, PROGRAMS_ID, entry) VALUES ('EUROSPORT1', 9991, 0);
INSERT INTO TVCHANNEL_TVPROGRAM (TVCHANNEL_ID, PROGRAMS_ID, entry) VALUES ('EUROSPORT1', 9992, 1);
INSERT INTO TVCHANNEL_TVPROGRAM (TVCHANNEL_ID, PROGRAMS_ID, entry) VALUES ('EUROSPORT1', 9993, 2);
INSERT INTO TVCHANNEL_TVPROGRAM (TVCHANNEL_ID, PROGRAMS_ID, entry) VALUES ('EUROSPORT1', 9994, 3);


INSERT INTO FESTIVAL (ID, NAME) VALUES (5, 'Festival Sundance de Cinema');
INSERT INTO FESTIVALDETAILS (ID, COUNTRY, HAPPENSAT, LOCALITY, FESTIVAL_ID) VALUES (15, 'USA', '2019-01-01 10:00:00.000000000', 'Chicago', 5);

INSERT INTO FESTIVAL (ID, NAME) VALUES (6, 'Tribeca');
INSERT INTO FESTIVALDETAILS (ID, COUNTRY, HAPPENSAT, LOCALITY, FESTIVAL_ID) VALUES (16, 'USA', '2019-02-01 10:00:00.000000000', 'Huston', 6);

