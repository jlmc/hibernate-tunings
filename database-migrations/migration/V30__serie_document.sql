create table SerieDocument
(
    id int
        constraint SerieDocument_pk
            primary key,
    indicator int default 0,
    name varchar(10) not null
);

create or replace function get_rec_nr_serie_document(_id integer) returns setof record language plpgsql
as
$$
declare
    vSerieDoc	int;
    REC		    record;

begin

    select indicator into vSerieDoc from SerieDocument where id = _id;
    update SerieDocument set indicator = (vSerieDoc + 1) where id= _id;
    select into REC id, indicator, name  from SerieDocument where id = _id;
    return next REC;

end;
$$;

insert into SerieDocument (id, indicator, name) values (1, 0, 'ex-0');

--select id as serieDocumentoId, name as title, indicator as numDoc from get_rec_nr_serie_document(1) as datos(id int, indicator int, name varchar) ;
--select id, indicator, name from get_rec_nr_serie_document(1) as datos(id int, indicator int, name varchar) ;
