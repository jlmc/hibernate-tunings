alter table if exists developer_programmig_language
  drop constraint if exists fk_developer_programmig_language__programming_language;

alter table if exists programming_language
  drop constraint if exists uk_programming_language__name;

alter table if exists developer_programmig_language
  drop constraint if exists fk_developer_programmig_language__developer;

drop table if exists developer cascade;

drop table if exists developer_programmig_language cascade;

drop table if exists programming_language cascade;

create table developer (
  id  bigserial not null,
  licence_number varchar(255),
  name varchar(255),
  primary key (id)
);

create table developer_programmig_language (
  developer_id int8 not null,
  programming_language_id int8 not null,
  primary key (developer_id, programming_language_id)
);

create table programming_language (
   id  bigserial not null,
   name varchar(255) not null,
   primary key (id)
);

alter table if exists developer
   add constraint uk_developer__licence_number unique (licence_number);

alter table if exists developer_programmig_language
  add constraint fk_developer_programmig_language_programming_language
  foreign key (programming_language_id)
  references programming_language;

alter table if exists developer_programmig_language
  add constraint fk_developer_programmig_language_developer
  foreign key (developer_id)
  references developer;

 alter table if exists programming_language
       add constraint uk_programming_language__name unique (name);