create sequence event_identifier_seq start with 1 increment by 6;
create sequence occurrence_seq start with 1 increment by 3;
create table event_identifier(id integer not null, primary key (id));
create table occurrence(id integer not null, description varchar(255), event_id integer, stamp timestamp not null default now(), primary key (id));
-- alter table occurrence add constraint FKrw85j4n8cn4x95mk0wjuyq9oq foreign key (event_id) references event_identifier;

/*
create table Athlete (
       id integer generated by default as identity,
        born_at date not null,
        name varchar(255),
        rank Integer not null default NEXTVAL('rank_id_seq'),
        stamp timestamp not null default now(),
        primary key (id)
    )
*/

create sequence rank_id_seq start with 1 increment by 1;

create table athlete ( id integer generated by default as identity primary key, born_at date not null, name varchar(255), rank Integer not null default NEXTVAL('rank_id_seq'), stamp timestamp not null default now());
