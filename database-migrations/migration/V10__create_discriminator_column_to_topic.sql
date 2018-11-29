alter table topic add column type smallint not null;

comment on column topic.type is '0 - Post, 1 - announcement';

create index topic_type_index on topic (type);

