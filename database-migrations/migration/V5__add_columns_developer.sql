ALTER TABLE depeloper
  ADD version INTEGER NOT NULL;

create table event_developer (
  event_id     bigint not null references event,
  developer_id bigint not null references depeloper,
  primary key (event_id, developer_id)
);