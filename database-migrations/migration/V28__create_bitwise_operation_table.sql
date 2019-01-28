drop table if exists operation;

create table operation
(
  id   bigint not null unique primary key,
  name varchar(20) not null,

  personal_options int default 0,
  enterprise_options int default 0

);