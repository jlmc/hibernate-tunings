create table client
(
  id   integer not null,
  name varchar(100),
  slug varchar (50) not null,

  primary key (id)
);

alter table client
  add constraint client_slug_uk
    unique (slug);
