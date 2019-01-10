do
$$
begin
  if not exists (select column_name
                 from information_schema.columns
                 where table_schema='public' and table_name='developer' and column_name='licence_number') then

    alter table developer add licence_number varchar(255);

    create unique index developer_licence_number_uindex on developer (licence_number);

  else
    raise notice 'public.developer.licence_number already exists';
  end if;


  if not exists (select table_name from information_schema.tables where table_schema = 'public' and table_name = 'programming_language') then

    create table programming_language (
      id  bigserial not null,
      name varchar(255) not null,
      primary key (id)
    );

    alter table if exists programming_language add constraint uk_programming_language__name unique (name);

  else
    raise notice 'public.programming_language already exists';
  end if;



  if not exists (select table_name from information_schema.tables where table_schema = 'public' and table_name = 'developer_programmig_language') then

    create table developer_programmig_language (
      developer_id int8 not null,
      programming_language_id int8 not null,
      primary key (developer_id, programming_language_id)
    );

    --alter table if exists developer_programmig_language
    alter table developer_programmig_language
       add constraint fk_developer_programmig_language_programming_language foreign key (programming_language_id) references programming_language;

    --alter table if exists developer_programmig_language
    alter table developer_programmig_language
      add constraint fk_developer_programmig_language_developer foreign key (developer_id) references developer;

  else
    raise notice 'public.developer_programmig_language already exists';
  end if;

end
$$

-- alter table if exists developer_programmig_language drop constraint if exists fk_developer_programmig_language__programming_language;
-- alter table if exists programming_language drop constraint if exists uk_programming_language__name;
-- alter table if exists developer_programmig_language drop constraint if exists fk_developer_programmig_language__developer;
-- drop table if exists developer cascade;
-- drop table if exists developer_programmig_language cascade;
-- drop table if exists programming_language cascade;
