# Hints


## JPA

* javax.persistence.query.timeout

    Defined the number of **milliseconds** a given JPA query is allowed to run for. Behind the scenes, this hint will instruct Hibernate to call the `PreparedStatement.setQueryTimeout` method for the associated SQL query that gets executed.


* javax.persistence.loadgraph

    JPA 2.1 introduced the `javax.persistence.loadgraph` query hint to provide a query-specific fetch graph that overrides the default fetch plan defined by the entity mapping.

    According to the JPA specification, the `javax.persistence.loadgraph` query hint **should fetch eagerly only the associations that are explicitly specified by the currently provided fetch graph while the remaining association should be fetched according to their mapping fetching strategy**.

    The javax.persistence.loadgraph query hint can be specified as follows:
    
    ```java
    Project project = entityManager
    .find(Project.class, 1L, Map.of(
            "javax.persistence.loadgraph", entityManager.getEntityGraph("Project.comments")
        ));
    ```


* javax.persistence.fetchgraph

    JPA 2.1 introduced the **javax.persistence.fetchgraph** query hint as well to provide a query-specific fetch graph that overrides the default fetch plan defined by the entity mapping.

    According to the JPA specification, the **javax.persistence.fetchgraph** query hint should **fetch eagerly only the associations that are explicitly specified by the currently provided fetch graph while the remaining association should be fetched lazily**.

    However, _because lazy fetching is a non-mandatory requirement_, the Hibernate behavior for the `javax.persistence.fetchgraph` query hint is different as the associations not specified by the provided fetch graph are fetched according to their entity mapping fetching strategy.



## Hibernate query hints

The Hibernate query hints defined by the QueryHints class can be summarized as follows:




| Query hint name                     | QueryHints constant   | Description     |
| ----------------------------------- |:--------------------- | :---------------|
| org.hibernate.cacheMode             | CACHE_MODE            | Equivalent to `org.hibernate.query.Query#setCacheMode` |
| org.hibernate.cacheRegion           | CACHE_REGION          | Equivalent to `org.hibernate.query.Query#setCacheRegion` |
| org.hibernate.cacheable             | CACHEABLE             | Equivalent to `org.hibernate.query.Query#setCacheable` |
| org.hibernate.callable              | CALLABLE              | Useful for named queries that need to be executed using a `JDBC CallableStatement` |
| org.hibernate.comment               | COMMENT               | Equivalent to `org.hibernate.query.Query#setComment` |
| org.hibernate.fetchSize             | FETCH_SIZE            | Equivalent to `org.hibernate.query.Query#setFetchSize` |
| org.hibernate.flushMode             | FLUSH_MODE	          | Equivalent to `org.hibernate.query.Query#setFlushMode` |
| hibernate.query.followOnLocking     | FOLLOW_ON_LOCKING     | Override the `Dialect#useFollowOnLocking` setting |
| org.hibernate.lockMode              | NATIVE_LOCKMODE       | Specify a custom `javax.persistence.LockModeType` or` org.hibernate.LockMode` for the current query |
| hibernate.query.passDistinctThrough | PASS_DISTINCT_THROUGH | Prevent the JPQL or Criteria API `DISTINCT` keyword from being passed to the SQL query |
| org.hibernate.readOnly              | READ_ONLY             | Equivalent to `org.hibernate.query.Query#setReadOnly` |
| org.hibernate.timeout               | TIMEOUT_HIBERNATE     | Equivalent to `org.hibernate.query.Query#setTimeout`. The timout value is specified in seconds. |
| javax.persistence.query.timeout     | TIMEOUT_JPA           | Equivalent to org.hibernate.query.Query#setReadOnly. The timout value is specified in milliseconds. |

