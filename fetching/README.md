# Agenda

- NativeSQLSynchronizedEntityClassTest
- QueryHintFetchSizeTest
- DTOProjectionPaginationTest



# Query fetch size

When using JPA, the JDBC ResultSet is fully traversed and materialized into the expected query
result. 

For this reason, the fetch size can only influence the number of database roundtrips
required for fetching the entire ResultSet.

When using SQL Server, PostgreSQL, or MySQL, the ResultSet is fetched in a single database roundtrip. For these three relational database systems, the default fetch size setting is often the right choice for JPA queries.

On the other hand, Oracle uses a default fetch size of only 10 records. 

Considering the previous pagination query, the page size being also 10, the default fetch size does not influence the number of database roundtrips. However, if the page size is 50, then Hibernate will require 5 roundtrips to fetch the entire ResultSet.

Luckily, Hibernate can control the fetch size either on a query basis or at the EntityManagerFactory level.

At the query level, the fetch size can be configured using the org.hibernate.fetchSize hint:

```java
List<PostCommentSummary> summaries = entityManager.createQuery(
    "select new " +
    " io.costax.fetching.PostCommentSummary( " +
    " p.id, p.title, c.review ) " +
    "from PostComment c " +
    "join c.post p")
.setFirstResult(pageStart)
.setMaxResults(pageSize)
.setHint(QueryHints.HINT_FETCH_SIZE, pageSize)
.getResultList();
```
The default fetch size can also be configured as a configuration property:

`<property name="hibernate.jdbc.fetch_size" value="50"/>`

However, setting the default fetch size requires diligence because it affects every
executing SQL query. Like with any other performance tuning setting, measuring the
gain is the only way to determine if a settings makes sense or not.


# Fetching Entities Direct fetching


The easiest way to load an entity is to call the find method of the Java Persistence EntityManager interface.

```
final Project project1 = em.find(Project.class, 1L);
final Project project2 = em.unwrap(Session.class).get(Project.class, 2L);
```


When running either the find or the get method, Hibernate fires a *LoadEvent*. Without customizing event listeners, the **LoadEvent** is handled by the **DefaultLoadEventListener** class which tries to locate the entity as follows:

1. First, Hibernate tries to find the entity in the currently running Persistence Context (the first-level cache). 
Once an entity is loaded, Hibernate always returns the same object instance on any successive fetching requests, no matter if it is a query or a direct fetching call. 
This mechanism guarantees application-level repeatable reads.

2. If the entity is not found in the first-level cache and the second-level cache is enabled, Hibernate will try to fetch the entity from the second-level cache.

3. If the second-level cache is disabled or the entity is not found in the cache, Hibernate will execute a SQL query to fetch the requested entity.


    Not only the data access layer is much easier to implement this way, but Hibernate also offers strong data consistency guarantees. 
    Backed by the application-level repeatable reads offered by the first-level cache, the built-in optimistic concurrency control mechanism can prevent lost updates, even across successive web requests.
    
    While a SQL projection requires a database round trip to fetch the required data, entities can also be loaded from the second-level caching storage. By avoiding database calls, the entity caching mechanism can improve response time, while the database load can decrease as well._



## Fetching a Proxy reference

Alternatively, direct fetching can also be done lazily. For this purpose, the EntityManager must return a Proxy which delays the SQL query execution until the entity is accessed for the first time.

```
final Project reference = em.getReference(Project.class, 1L);

LOGGER.info("Loaded Project entity");
LOGGER.info("The project title is '{}'", reference.getTitle());
```


The getReference method call does not execute the SQL statement right away, so the Loaded project entity message is the first to be logged. 
When the Project entity is accessed by calling the getTitle method, Hibernate executes the select query and, therefore, loads the entity prior to returning the title attribute.



```
10:40:20,314 INFO  [io.costax.fetchingentities.DirectFetchingTest] - Loaded Project entity
10:40:20,321 DEBUG [org.hibernate.SQL] - 
    select
        project0_.id as id1_0_0_,
        project0_.version as version2_0_0_,
        project0_.title as title3_0_0_ 
    from
        project project0_ 
    where
        project0_.id=?

10:40:20,352 INFO  [io.costax.fetchingentities.DirectFetchingTest] - The project title is 'effective-java-3'
```


## Natural identifier fetching

Hibernate offers the possibility of loading an entity by its natural identifier (business key). 
The natural id can be either a single column or a combination of multiple columns that uniquely identifies a given database table row.

```
final EntityManager em = provider.em();
Session session = em.unwrap(Session.class);
Client client = session.bySimpleNaturalId(Client.class).load("ptech");

```

Hibernate will generate the following sql:

```
    select
        client_.id as id1_0_ 
    from
        client client_ 
    where
        client_.slug=?

    select
        client0_.id as id1_0_0_,
        client0_.create_on as create_o2_0_0_,
        client0_.name as name3_0_0_,
        client0_.slug as slug4_0_0_,
        client0_.version as version5_0_0_ 
    from
        client client0_ 
    where
        client0_.id=?
```


##