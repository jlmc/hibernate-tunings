do
  $$
    begin
      if not exists(
          select * from information_schema.tables where table_schema = 'public' and table_name = 'book'
        ) then
        create table book
        (
          publisher_id        int    not null,
          registration_number bigint not null generated by default as identity,
          title               varchar(255),
          version             int,
          primary key (publisher_id, registration_number)
        );

      else

        raise notice 'public.book already exists';

      end if;

    end
    $$
