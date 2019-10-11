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


  if not exists (select table_name from information_schema.tables where table_schema = 'public' and table_name = 'programing_language') then

    create table programing_language (
      id  bigserial not null,
      name varchar(255) not null,
      primary key (id)
    );

    alter table if exists programing_language add constraint uk_programing_language__name unique (name);

  else
    raise notice 'public.programing_language already exists';
  end if;



  if not exists (select table_name from information_schema.tables where table_schema = 'public' and table_name = 'developer_programing_language') then

    create table developer_programing_language (
      developer_id int8 not null,
      programing_language_id int8 not null,
      primary key (developer_id, programing_language_id)
    );

    --alter table if exists developer_programing_language
    alter table developer_programing_language
       add constraint fk_developer_programing_language_programing_language foreign key (programing_language_id) references programing_language;

    --alter table if exists developer_programing_language
    alter table developer_programing_language
      add constraint fk_developer_programing_language_developer foreign key (developer_id) references developer;

  else
    raise notice 'public.developer_programing_language already exists';
  end if;

end
$$

-- alter table if exists developer_programing_language drop constraint if exists fk_developer_programing_language__programing_language;
-- alter table if exists programing_language drop constraint if exists uk_programing_language__name;
-- alter table if exists developer_programing_language drop constraint if exists fk_developer_programing_language__developer;
-- drop table if exists developer cascade;
-- drop table if exists developer_programing_language cascade;
-- drop table if exists programing_language cascade;
