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