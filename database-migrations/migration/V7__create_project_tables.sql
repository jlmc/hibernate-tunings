create table project
(
   id bigint generated by default as identity,
   version integer not null,
   title text not null,
   primary key (id)
);

create table issue
(
  id bigint generated by default as identity,
  version integer not null,
  project_id bigint references project,
  title text not null,
  description text null,
  create_at timestamp with time zone not null,
  primary key (id)
);

create index issue_project_id_create_at_index on issue (project_id, create_at);