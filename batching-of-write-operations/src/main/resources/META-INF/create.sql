create schema if not exists multimedia;

drop table if exists multimedia.report cascade;
drop table if exists multimedia.serie_actor cascade;
drop table if exists multimedia.actor cascade;
drop table if exists multimedia.ssx cascade;
drop sequence if exists multimedia.actor_many_seq;
drop sequence if exists multimedia.serie_many_seq;


create sequence multimedia.serie_many_seq increment by 5;

create table multimedia.actor
(
  id        bigint not null primary key,
  firstname varchar,
  lastname  varchar,
  version   integer
);

create table multimedia.ssx
(
  id          integer not null primary key,
  description varchar,
  title       varchar,
  version     interval
);

create table multimedia.serie_actor
(
  serie_id int8 not null,
  actor_id int8 not null,
  primary key (serie_id, actor_id)
);

create sequence multimedia.actor_many_seq increment by 10;

create table multimedia.report
(
  id     integer not null primary key,
  estado integer,
  name   varchar
);

