# How to map a JPA entity to a View or SQL query using Hibernate


Database views, in general, are mapped in the same way as database tables. 
We can just have to define an entity that maps the view with the specific name and one or more of its columns.

But the normal table mapping is not read-only, and you can use the entity to change its content.

Depending on the database you use and the definition of the view, you’re not allowed to perform an update on the view content. 
We should therefore also prevent Hibernate from updating it.

To do that we can easily achieve this with the Hibernate-specific **`@Immutable`** annotation.

The @Immutable annotation tells Hibernate to ignore all changes on this entity, but you can use it to retrieve data from the database.



## How To Map an Entity to a Query

Hibernate allow us also to map an entity to a query instead of a database table.


##### When should you use this mapping

Instead of mapping an entity to an SQL query, you could also:

1. Execute a query and map the result to a DTO.
2. Create a database view and map it to an entity.

Depending on your use case, both options offer certain benefits.

If you can execute additional queries to retrieve the required information, a query with a DTO projection is the better choice.


#### How to implement the mapping

Hibernate’s **`@Subselect`** annotation to map an entity to an SQL query. 

Before you use this mapping, you need to be aware of two side effects:


1. **We can’t use this entity to perform any write operations**. 

    - Hibernate would try to execute the operation on the SQL statement provided by the **`@Subselect`** annotation. 
    - You should, therefore, annotate the entity with **`@Immutable`**, use the field-based access strategy and omit all setter methods.

2. Hibernate doesn’t know which database tables are used by the SQL statement configured in the **`@Subselect`** annotation.
 
   - You can provide this information by annotating the entity with **`@Synchronize`**. 
   - That enables Hibernate to flush pending state transitions.


```java

@Entity
@Subselect(
    "SELECT b.id, b.title, count(r) as numreviews "
    + "FROM Book b LEFT JOIN Review r ON b.id = r.book_id "
    + "GROUP BY b.id, b.title")
@Synchronize({"book", "review"})
@Immutable
public class XptoSummary {
 
    @Id
    private Long id;
 
    private String title;
 
    private int numReviews;
 
    @OneToMany(mappedBy = "book")
    private Set<Review> reviews;
}


```

---

## About the examples

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