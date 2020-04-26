create table account
(
    id    uuid not null
        constraint account_pkey
            primary key,
    owner varchar(255),
    value numeric(19, 2)
);
