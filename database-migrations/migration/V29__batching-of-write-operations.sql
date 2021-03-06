create schema if not exists multimedia;

drop table if exists multimedia.review cascade;
drop table if exists multimedia.book_author cascade;
drop table if exists multimedia.author cascade;
drop table if exists multimedia.book cascade;
drop sequence if exists multimedia.author_10_seq;
drop sequence if exists multimedia.book_5_seq;

create sequence multimedia.author_10_seq increment by 10 start with 50;
create sequence multimedia.book_5_seq increment by 5 start with 50;

create table multimedia.author
(
    id        bigint not null primary key,
    firstname varchar,
    lastname  varchar,
    version   integer,
    mod_date timestamp with time zone
);

create table multimedia.book
(
    id          integer not null primary key,
    description varchar,
    title       varchar,
    version     integer
);

create table multimedia.book_author
(
    book_id   int8 not null references multimedia.book (id),
    author_id int8 not null references multimedia.author (id),
    primary key (book_id, author_id)
);


create table multimedia.review
(
    id      integer not null primary key,
    book_id int8    not null references multimedia.book (id),
    rating  integer,
    comment varchar(100),
    version integer
);


create table multimedia.publisher
(
    id int constraint publisher_pk primary key,
    mod_date timestamp with time zone,
    name varchar(150)
);


