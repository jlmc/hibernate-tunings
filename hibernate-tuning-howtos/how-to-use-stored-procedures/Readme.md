## A little bit of information about stored procedures in Postgres



A stored procedure and user-defined function (UDF) is a set of SQL and procedural statements (declarations, assignments, loops, flow-of-control etc.) that stored on the database server and can be invoked using the SQL interface.

```postgresplsql
CREATE OR REPLACE FUNCTION increment(i INT) RETURNS INT AS $$
BEGIN
  RETURN i + 1;
END;
$$ LANGUAGE plpgsql;

-- An example how to use the function (Returns: 11)
SELECT increment(10);
```

### Before postgres 11

In PostgreSQL, both **stored procedures** and **user-defined functions** are created with **CREATE FUNCTION** statement. 
There are differences between the notion of stored procedures and functions in database systems:


|                                 | Stored Procedure  | Function                   |
|:--------------------------------|:-----------------:|:--------------------------:|
| Use in an expression            | NO                | YES                        |
| Return a value                  | NO                | YES                        |
| Return values as OUT parameters | YES               | NO                         |
| Return a single result set      | YES               | YES (as a table function)  |
| Return multiple result sets     | YES               | NO                         |



So in most cases, the purpose of a stored procedure is to:

* Perform actions without returning any result (**INSERT, UPDATE operations i.e.**)
* Return one or more scalar values as OUT parameters
* Return one or more result sets


Usually the purpose of a user-defined function is to process the input parameters and return a new value.

###### Reporting Tools

Many reporting tools (Crystal Reports, Reporting Services, BI tools etc.) allow you to specify a query (SQL SELECT statement) or a stored procedure returning a result set to define a data set for a report.

Stored procedures are very useful when you need to perform complex calculations before the data is available for a report.

###### Stored Procedures in PostgreSQL
Usually stored procedures do not return any value, or return one or more result sets.


**No Value Returned**


If a stored procedure does not return any value, you can specify void as the return type:

```postgresplsql
-- Procedure to insert a new project returning a void
CREATE OR REPLACE FUNCTION add_project(ptitle VARCHAR(100))
  RETURNS void AS $$
BEGIN
  --INSERT INTO project (version, title) VALUES (0, ptitle) returning id;
  INSERT INTO project (version, title) VALUES (0, ptitle);
END;
$$ LANGUAGE plpgsql;

-- Add a new project
SELECT add_project('Mega hiper project');

-- we can also use **PERFORM add_project()** statement to invoke add_city from another procedure or function.

```



**Return a Single Result Set - Return a Cursor**

To return a result set from a PostgreSQL procedure, we have to specify refcursor return type, open and return a cursor:

```postgresplsql
CREATE OR REPLACE FUNCTION show_projects() RETURNS refcursor AS $$
DECLARE
  ref refcursor;
BEGIN
  OPEN ref FOR SELECT id, version, title FROM project;
  RETURN ref;
END;
$$ LANGUAGE plpgsql;
```


**Important Note**: The cursor remains open until the end of transaction, and since PostgreSQL works in auto-commit mode by default, the cursor is closed immediately after the procedure call, so it is not available to the caller. To work with cursors you have to start a transaction (turn auto-commit off).



### Postgres 11

we postgres 11 we will finally have it stored procedure, CREATE PROCEDURE syntax. Traditionally PostgreSQL has provided all the means to write functions (which were often simply called “stored procedures”). However:
 * in a function we cannot really run transactions 
 * all we can do is to use exceptions, which are basically savepoints. 
 * Inside a function you cannot just commit a transaction or open a new one. 
 
 CREATE PROCEDURE will change all that and provide you with the means to run transactions in procedural code.

```postgresplsql
CREATE PROCEDURE test_proc()
       LANGUAGE plpgsql
AS $$
  BEGIN
    CREATE TABLE a (aid int);
    CREATE TABLE b (bid int);
    COMMIT;
    CREATE TABLE c (cid int);
    ROLLBACK;
  END;
$$;
```

The first thing to notice here is that there is a COMMIT inside the procedure. In classical PostgreSQL functions this is not possible for a simple reason. Consider the following code:

```postgresplsql
SELECT func(id) FROM large_table;
```


What would happen if some function call simply commits? 
Totally chaos would be the consequence. Therefore real transactions are only possible inside a “procedure”, which is never called the way a function is executed. Also: Note that there is more than just one transaction going on inside our procedure. A procedure is therefore more of a “batch job”.

The following example shows, how to call the procedure I have just implemented:

```postgresplsql
CALL test_proc();
```
The first two tables where committed – the third table has not been created because of the rollback inside the procedure.

db18=# \d
List of relations
 Schema | Name | Type  | Owner
--------+------+-------+-------
 public | a    | table | hs
 public | b    | table | hs

(2 rows)