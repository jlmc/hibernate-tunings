create table auditlog
(
    id    uuid not null
        constraint auditlog_pkey
            primary key,
    at    timestamp,
    owner varchar(255),
    value numeric(19, 2),
    comment varchar
);
