create schema exchange

create table exchange.tread
(
    id    int8 not null,
    value int2 not null,
    primary key (id)
);

create sequence exchange.tread_sec increment by 25;