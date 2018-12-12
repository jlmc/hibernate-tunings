create schema if not exists tasks;

create table tasks.todo (
  id bigint null primary key,
  title varchar(100) not null,
  deleted boolean default false not null
);

create table tasks.todo_comment (
  id bigint not null primary key,
  review varchar(100) not null,
  todo_id bigint references tasks.todo(id),
  deleted boolean default false not null
);

create index todo_comment_todo_id_index on tasks.todo_comment (todo_id);

create table tasks.todo_details (
  id bigint references tasks.todo(id),
  created_on timestamp with time zone not null,
  created_by varchar (100) null,
  deleted boolean default false not null,
  primary key (id)
);

create table tasks.tag (
  id bigint not null,
  deleted boolean default false not null,
  name varchar (20),
  primary key (id)
);


create table tasks.todo_tag (
  todo_id bigint not null references tasks.todo (id),
  tag_id bigint not null references tasks.tag (id),
  primary key (todo_id, tag_id)
);

create index todo_tag_todo_id_index on tasks.todo_tag (todo_id);
create index todo_tag_tog_id_index on tasks.todo_tag (tag_id);
