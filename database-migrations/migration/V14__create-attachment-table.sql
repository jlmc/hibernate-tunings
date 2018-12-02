create schema if not exists communication;

create table communication.message
(
  id          uuid         not null,
  transmissor varchar(100) not null,
  receptor    varchar(100) not null,
  subject     varchar(15),
  body        text         not null,
  primary key (id)
);

create table communication.attachment
(
  id          uuid    not null,
  message_id  uuid    not null references communication.message (id),

  file_name   varchar not null,
  size        integer default 0,
  description varchar(1024),
  file bytea,

  primary key (id)
);

create table communication.image
(
  id         uuid not null primary key,
  message_id uuid not null references communication.message (id),

  file       oid,
  type       varchar(255)

);

comment on column communication.attachment.file is 'very big file stream';
