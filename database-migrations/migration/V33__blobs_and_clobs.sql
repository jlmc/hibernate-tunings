drop table if exists article_blob;
drop table if exists article_clob;

create table article_blob
(
    id      bigint primary key,
    content text,
    cover   oid,
    title   character varying(255)
);

create table article_clob
(
    id      bigint primary key,
    content text,
    cover   oid,
    title   character varying(255)
);