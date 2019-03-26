create schema multimedia;

drop table if exists multimedia.actor cascade;

drop table if exists multimedia.serie cascade;

drop sequence if exists multimedia.actor_many_seq;
drop sequence if exists multimedia.serie_many_seq



create table multimedia.serie
(
  id          int8 not null,
  description varchar(255),
  title       varchar(255),
  version     int4,
  primary key (id)
)

-- auto-generated definition
create sequence multimedia.serie_many_seq
  increment by 5;



create table actor
(
  id        bigint not null
    constraint actor_pkey
      primary key,
  firstname varchar(255),
  lastname  varchar(255),
  version   integer
);



create table multimedia.serie_actor
(
  serie_id int8 not null,
  actor_id int8 not null,
  primary key (serie_id, actor_id)
);

create sequence actor_many_seq
  increment by 10;