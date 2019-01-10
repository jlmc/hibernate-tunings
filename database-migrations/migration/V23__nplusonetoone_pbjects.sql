CREATE SCHEMA IF NOT EXISTS nplusonetoone;

alter table if exists nplusonetoone.Cc drop constraint if exists FK6gvfurcp6ajx0jkj00bfi28pc;
alter table if exists nplusonetoone.Document drop constraint if exists FKfo1m6fayado0kkvo3xgpc670l;

drop table if exists nplusonetoone.Cc cascade;
drop table if exists nplusonetoone.Dependency cascade;
drop table if exists nplusonetoone.Document cascade;
drop table if exists nplusonetoone.humanresource cascade;

create table nplusonetoone.humanresource (
  id  serial not null primary key,
  username varchar not null unique,
  name varchar
);

create table nplusonetoone.Cc (
  id  serial not null,
  description varchar(255),
  dependency_id int4,
  rh_id int4 references nplusonetoone.humanresource,
  primary key (id)
);

create table nplusonetoone.Dependency (
  id int4 not null,
  code varchar(255),
  primary key (id)
);

create table nplusonetoone.Document (
  id  serial not null,
  createdAt timestamp,
  cc_id int4,
  primary key (id)
);

alter table if exists nplusonetoone.Dependency add constraint UK_m9e6y2pffutlm74h25n281k8w unique (code);

alter table if exists nplusonetoone.Cc
       add constraint FK6gvfurcp6ajx0jkj00bfi28pc
       foreign key (dependency_id)
       references nplusonetoone.Dependency;

alter table if exists nplusonetoone.Document
       add constraint FKfo1m6fayado0kkvo3xgpc670l
       foreign key (cc_id)
       references nplusonetoone.Cc;


-- default data

insert into nplusonetoone.Dependency (id, code) values (1, 'A-1');
insert into nplusonetoone.Dependency (id, code) values (2, 'AZ-2');
insert into nplusonetoone.Dependency (id, code) values (3, 'XD-3');
insert into nplusonetoone.Dependency (id, code) values (4, 'B-4');


insert into nplusonetoone.humanresource (id, username, name)  values (1, 'duke', 'The Java Duke');
insert into nplusonetoone.humanresource (id, username, name)  values (2, 'costax', 'Joao Costa');
insert into nplusonetoone.humanresource (id, username, name)  values (3, 'airjordan', 'M. Jordan');

insert into nplusonetoone.cc (id, rh_id, dependency_id, description) VALUES (1, 1, 1, 'Moviment 1');
insert into nplusonetoone.document (id, createdat, cc_id) VALUES (1, '2018-09-19 21:22:25.160000', 1);

insert into nplusonetoone.cc (id, rh_id, dependency_id, description) VALUES (2, 2, 1, 'Moviment 2');
insert into nplusonetoone.document (id, createdat, cc_id) VALUES (2, '2018-09-19 21:22:25.160000', 2);

insert into nplusonetoone.cc (id, rh_id, dependency_id, description) VALUES (3, 3, 3, 'Moviment 3');
insert into nplusonetoone.document (id, createdat, cc_id) VALUES (3, '2018-09-19 21:22:25.160000', 3);

insert into nplusonetoone.cc (id, rh_id, dependency_id, description) VALUES (4, 1, 3, 'Moviment 3');
insert into nplusonetoone.document (id, createdat, cc_id) VALUES (4, '2018-09-18 21:22:25.160000', 4);

insert into nplusonetoone.cc (id, rh_id, dependency_id, description) VALUES (5, 2, 4, 'Moviment 3');
insert into nplusonetoone.document (id, createdat, cc_id) VALUES (5, '2018-09-17 21:22:25.160000', 5);

alter sequence nplusonetoone.humanresource_id_seq restart with 4;
alter sequence nplusonetoone.document_id_seq restart with 6;
alter sequence nplusonetoone.cc_id_seq restart with 6;