-- Function increments the input value by 1
CREATE OR REPLACE FUNCTION increment(i INT) RETURNS INT AS $$
BEGIN
  RETURN i + 1;
END;
$$ LANGUAGE plpgsql;

-- An example how to use the function (Returns: 11)
--SELECT increment(10);


-- Procedure to insert a new project returning a void
CREATE OR REPLACE FUNCTION add_project(ptitle VARCHAR(100))
  --RETURNS void AS $$
  RETURNS INTEGER AS $$
DECLARE
  nId INTEGER := NULL;
BEGIN
   INSERT INTO project (version, title) VALUES (0, ptitle) RETURNING project.id INTO nId;
  --INSERT INTO project (version, title) VALUES (0, ptitle);
  return nId;
END;
$$ LANGUAGE plpgsql;

-- Add a new project
--SELECT add_project('Mega hiper project');


CREATE OR REPLACE FUNCTION get_projects(pattern varchar)
  RETURNS refcursor AS
$BODY$
DECLARE
  projects refcursor;           -- Declare cursor variables
BEGIN
  OPEN projects FOR SELECT * FROM project WHERE title like pattern;
  RETURN projects;
END;
$BODY$
LANGUAGE plpgsql;


