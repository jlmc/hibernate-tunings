drop view if exists project_issues;

create or replace view project_issues as
select p.id as project_id, p.title as project_title, count(i.id) as number_of_issues
from project p
         left join issue i on p.id = i.project_id
group by p.id
order by number_of_issues desc
;


drop function if exists public.project_count_issues;

CREATE OR REPLACE FUNCTION public.project_count_issues(IN projectId bigint,
                                                       OUT commentCount bigint)
    RETURNS bigint AS
$$
BEGIN
    SELECT COUNT(*)
    INTO commentCount
    FROM issue
    WHERE project_id = projectId;
END;
$$
    LANGUAGE plpgsql VOLATILE
                     COST 100;


drop function if exists public.project_issues;

CREATE OR REPLACE FUNCTION public.project_issues(projectId bigint)
    RETURNS refcursor AS
$$
DECLARE
    projectIssuess REFCURSOR;
BEGIN
    OPEN projectIssuess FOR
        SELECT *
        FROM project_issues
        WHERE project_id = projectId;
    RETURN projectIssuess;
END;
$$
    LANGUAGE plpgsql VOLATILE
                     COST 100;



SELECT functions.routine_name               as name,
       string_agg(functions.data_type, ',') as params
FROM (
         SELECT routines.routine_name,
                parameters.data_type,
                parameters.ordinal_position
         FROM information_schema.routines
                  LEFT JOIN
              information_schema.parameters
              ON
                  routines.specific_name = parameters.specific_name
         WHERE routines.specific_schema = 'public'
         ORDER BY routines.routine_name,
                  parameters.ordinal_position
     ) AS functions
GROUP BY functions.routine_name;

/*

with functions as (

    SELECT routines.routine_name,
           parameters.data_type,
           parameters.ordinal_position
    FROM information_schema.routines
             LEFT JOIN
         information_schema.parameters
         ON
                 routines.specific_name = parameters.specific_name
    WHERE routines.specific_schema = 'public'
    ORDER BY routines.routine_name,
             parameters.ordinal_position


)

SELECT functions.routine_name               as name,
       string_agg(functions.data_type, ',') as params
from functions
GROUP BY functions.routine_name;

SELECT functions.routine_name               as name,
       string_agg(functions.data_type, ',') as params
FROM (
         SELECT routines.routine_name,
                parameters.data_type,
                parameters.ordinal_position
         FROM information_schema.routines
                  LEFT JOIN
              information_schema.parameters
              ON
                      routines.specific_name = parameters.specific_name
         WHERE routines.specific_schema = 'public'
         ORDER BY routines.routine_name,
                  parameters.ordinal_position
     ) AS functions
GROUP BY functions.routine_name;


 */