alter table if exists Cc drop constraint if exists FK6gvfurcp6ajx0jkj00bfi28pc;
alter table if exists Document drop constraint if exists FKfo1m6fayado0kkvo3xgpc670l;

drop table if exists Cc cascade;
drop table if exists Dependency cascade;
drop table if exists Document cascade;

create table Cc (
  id  serial not null,
  description varchar(255),
  dependency_id int4,
  primary key (id)
);

create table Dependency (
  id int4 not null,
  code varchar(255),
  primary key (id)
);

create table Document (
  id  serial not null,
  createdAt timestamp,
  cc_id int4,
  primary key (id)
);

alter table if exists Dependency
       add constraint UK_m9e6y2pffutlm74h25n281k8w unique (code);

alter table if exists Cc
       add constraint FK6gvfurcp6ajx0jkj00bfi28pc
       foreign key (dependency_id)
       references Dependency;

alter table if exists Document
       add constraint FKfo1m6fayado0kkvo3xgpc670l
       foreign key (cc_id)
       references Cc;