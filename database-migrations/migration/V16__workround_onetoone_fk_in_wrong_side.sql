create schema if not exists workarounds;

create table workarounds.game_report (
  id bigserial primary key,
  resisted timestamp with time zone not null default now(),
  score_home smallint default 0 not null,
  score_visitor smallint default 0 not null,
  events bytea
);

create table workarounds.game (
  id bigserial primary key,
  home varchar(100) not null,
  visitor varchar(100) not null,
  game_report_id bigint references workarounds.game_report(id)
);