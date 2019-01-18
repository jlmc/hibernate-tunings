drop table if exists configuration;

create table configuration (
   id bigint generated by default as identity,
   tenant varchar(10) not null unique,

   roles text[] default array[]::text[],
   numbers int[] default array []::int[]

);