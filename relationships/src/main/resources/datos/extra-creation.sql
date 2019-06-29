alter table TVCHANNEL_TVPROGRAM add column if not exists entry int default 0 not null;
alter table movie_actor_personage  add column if not exists entry int default 0 not null;