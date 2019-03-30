delete from multimedia.book_author where true;
delete from multimedia.review where true;
delete from multimedia.book where true;
delete from multimedia.author where true;

INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (1, 'Joshua', 'Bloch', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (2, 'Gavin', 'King', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (3, 'Christian', 'Bauer', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (4, 'Gary', 'Gregory', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (5, 'Raoul-Gabriel', 'Urma', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (6, 'Mario', 'Fusco', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (7, 'Alan', 'Mycroft', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (8, 'Andrew Lee', 'Rubinger', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (9, 'Aslak', 'Knutsen', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (10, 'Bill', 'Burke', 0);
INSERT INTO multimedia.author (id, firstname, lastname, version) VALUES (11, 'Scott', 'Oaks', 0);


INSERT INTO multimedia.book (id, description, title, version) VALUES (1, '2008-05-08', 'Effective Java', 0);
INSERT INTO multimedia.book (id, description, title, version) VALUES (2, '2015-10-01', 'Java Persistence with Hibernate', 0);
INSERT INTO multimedia.book (id, description, title, version) VALUES (3, '2014-08-28', 'Java 8 in Action', 0);
INSERT INTO multimedia.book (id, description, title, version) VALUES (4, '2014-03-12', 'Continuous Enterprise Development in Java', 0);
INSERT INTO multimedia.book (id, description, title, version) VALUES (5, '2010-09-08', 'Enterprise JavaBeans 3.1', 0);
INSERT INTO multimedia.book (id, description, title, version) VALUES (6, '2014-04-29', 'Java Performance The Definitive Guide', 0);


INSERT INTO multimedia.book_author (book_id, author_id) VALUES (1, 1);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (2, 2);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (2, 3);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (2, 4);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (3, 5);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (3, 6);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (3, 7);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (4, 8);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (4, 9);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (5, 8);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (5, 10);
INSERT INTO multimedia.book_author (book_id, author_id) VALUES (6, 11);


INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (1, 1, 11, 'comment dummy', 0);
INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (2, 1, 12, 'in progress 1', 0);
INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (3, 2, 13, 'done 1', 0);
INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (4, 3, 12, 'in progress 2', 1);
--INSERT INTO multimedia.review (id, book_id, rating, comment, version) VALUES (5, 6, null, 'no status', 0);

