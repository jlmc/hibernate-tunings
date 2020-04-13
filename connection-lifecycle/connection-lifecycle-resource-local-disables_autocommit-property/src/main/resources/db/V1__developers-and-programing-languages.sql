alter table if exists developer_programing_language
  drop constraint if exists fk_developer_programing_language__programing_language;

alter table if exists programing_language
  drop constraint if exists uk_programing_language__name;

alter table if exists developer_programing_language
  drop constraint if exists fk_developer_programing_language__developer;

drop table if exists developer cascade;

drop table if exists developer_programing_language cascade;

drop table if exists programing_language cascade;

create table developer (
  id  bigserial not null,
  licence_number varchar(255),
  name varchar(255),
  primary key (id)
);

create table developer_programing_language (
  developer_id int8 not null,
  programing_language_id int8 not null,
  primary key (developer_id, programing_language_id)
);

create table programing_language (
   id  bigserial not null,
   name varchar(255) not null,
   primary key (id)
);

alter table if exists developer
   add constraint uk_developer__licence_number unique (licence_number);

alter table if exists developer_programing_language
  add constraint fk_developer_programing_language_programing_language
  foreign key (programing_language_id)
  references programing_language;

alter table if exists developer_programing_language
  add constraint fk_developer_programing_language_developer
  foreign key (developer_id)
  references developer;

 alter table if exists programing_language
       add constraint uk_programing_language__name unique (name);