# How to map a JPA entity to a View or SQL query using Hibernate

We want to know all the PostgreSQL functions we can call, and, for this purpose, we can use the following SQL query:

```SQL


SELECT
    functions.routine_name as name,
    string_agg(functions.data_type, ',') as params
FROM (
         SELECT
             routines.routine_name,
             parameters.data_type,
             parameters.ordinal_position
         FROM
             information_schema.routines
                 LEFT JOIN
             information_schema.parameters
             ON
                     routines.specific_name = parameters.specific_name
         WHERE
                 routines.specific_schema='public'
         ORDER BY
             routines.routine_name,
             parameters.ordinal_position
     ) AS functions
GROUP BY functions.routine_name

```


If we get a "Schema-validation: missing table" (even with jpa.hibernate.ddl-auto set to none).

One of the reasons could be like the "jpa.hibernate.ddl-auto" was not properly applied. 
Maybe there's a configuration overriding it.
Add a breakpoint in SessionFactoryOptionsBuilder at the line in the image attached.

```
		try {
			this.schemaAutoTooling = SchemaAutoTooling.interpret( (String) configurationSettings.get( AvailableSettings.HBM2DDL_AUTO ) );
		}
		catch (Exception e) {
			log.warn( e.getMessage() + "  Ignoring" );
		}
```