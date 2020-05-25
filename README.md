# Performance Note Java Persistence and Hibernate

## Performance Facts

- “More than half of application performance bottlenecks originate in the database - [http://www.appdynamics.com/database/](http://www.appdynamics.com/database/)

- “Like us, our users place a lot of value in speed — that's why we've decided to take site speed into account in our search rankings.”
[Google Ranking](https://webmasters.googleblog.com/2010/04/using-site-speed-in-web-search-ranking.html)

- “It has been reported that every 100ms of latency costs Amazon 1% of profit."
[http://radar.oreilly.com/2008/08/radar-theme-web-ops.html](http://radar.oreilly.com/2008/08/radar-theme-web-ops.html)

## JPA vs Hibernate

- JPA is only a specification. It describes the interfaces that the client operates with and the standard object-relational mapping metadata (annotations, XML).

- Although it implements the JPA specification, Hibernate retains its native API for both backward compatibility and to accommodate non-standard features.


## HOW TO RUN THIS EXAMPLES

### 1. Postgres Data Base
 
First off all, we need to create a Postgres database with the name `hibernate-tunings` in the port `5432`. 
For example, we can use Docker to create that resource:

```shell script
docker run --name hibernate-tunings \
    -p 5432:5432 \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=postgresdemos \
    -v $(pwd)"/hibernate-tunings:/var/lib/postgresql/data" -d postgres:11.4
```

Or, simply run the docker-compose up command, which will raise a database and run the migrations for us:
```
docker-compose up
```

### 2. Run all modules in a single command

To run all modules in a single command, we can execute:

```shell script
mvn flyway:migrate -pl database-migrations \
 -Dflyway.configFiles=local.conf \
 -Dflyway.locations=filesystem:database-migrations/migration \
&& mvn clean install -pl helpers \
&& mvn clean package
```

### 3. Run Step by Step 


1. Run the migrations:

```shell script
mvn flyway:migrate -pl database-migrations \
 -Dflyway.configFiles=local.conf \
 -Dflyway.locations=filesystem:database-migrations/migration
```

Or, in alternative:

```shell script
cd database-migrations
mvn flyway:migrate -Dflyway.configFiles=local.conf
```

2. build and run all the examples.

```shell script
mvn clean install -pl helpers && mvn clean package
```

---

## AGENDA

1. **Get-Started**
    1. logging-sql-statements
        - [Typical causes of performance problems](docs/1-Get-Started/1-and-2-loggers-notas.md)
        - [Why We Need Logging](docs/1-Get-Started/README.md)
        - [logging-sql-statements-examples](logging-sql-statements/README.md)
    
    2. [schema-management](docs/1-Get-Started/1.4-schema-manager.md)

2. **Connections**    
    1. [Connection Manager](docs/2-Connections/Readme.md)
    2. [Connection Lifecycle](connection-lifecycle)
        - [JDBC Connection lifecycle](docs/2-Connections/Readme.md)
        - [Lifecycle](connection-lifecycle/README.md)
    3. [Hibernate Statistics](connection-lifecycle/HibernateStatistics.md)
    
3. **Types**
    1. [jpa-and-hibernate-types](types/jpa-and-hibernate-types.md)
    2. [custom-hibernate-types](types/custom-hibernate-types/custom-hibernate-type.md)
    3. [open source hibernate types](types/hibernate-open-source-custom-types-project/README.md)

4. Identifiers
   1. [Generating Primary Keys](docs/4-Identifiers/GeneratingPrimaryKeys.md)
   2. [Identifiers Generators and Natural Keys](docs/4-Identifiers/4.1-Identifier.md)
   3. [Hibernate Sequence Optimizers](docs/4-Identifiers/4.2-hibernate-sequence-optimizers.md)

5. **JPA Relationships Types**    
    1. [About](relationships/Readme.md) 
    2. [Equals and Hashcode methods](advanced-topics/src/test/java/io/costax/hibernatetunning/relationships/EqualsConsistentCheckTest.java)
    3. [OneToMany and ManyToOne](advanced-topics/src/test/java/io/costax/hibernatetunning/relationships/OneToManyAndManyToOneTest.java)
    4. [OneToOne Tradicional Bidimentional](relationships/src/test/java/io/costax/relationships/onetoone/OneToOneTradicionalBidimentionalTest.java)
    5. [OneToOne Unidirectional with MapsId](relationships/src/test/java/io/costax/relationships/onetoone/OneToOneUnidirectionalTest.java)
    6. [ManyToMany](relationships/src/test/java/io/costax/relationships/manytomany/ManyToManyAlternativeOneToManyTest.java)
    7. [ElementCollection](relationships/src/test/java/io/costax/relationships/elementcollections/ElementCollectionSingleColumnTest.java)
    8. [SecondaryTable](relationships/src/test/java/io/costax/relationships/secondarytable/SecondaryTableTest.java)
    
6. **Inheritance**  
    1. [SingleTable About](advanced-topics/src/test/java/io/costax/hibernatetunning/inheritance/SingleTable.md)
        - [SingleTable Example](advanced-topics/src/test/java/io/costax/hibernatetunning/inheritance/SingleTable.md)    
    2. DiscriminatorColumn
    3. [JoinedInheritance with JoinTable](advanced-topics/src/test/java/io/costax/hibernatetunning/inheritance/SingleTable.md)
    
7. **Persistence Context and Flushing** 
    1. [PersistenceContext_Flushing](docs/7-Persistence-Context/7.0-PersistenceContext.md)  
    2. [ActionQueue](docs/7-Persistence-Context/7.1-ActionQueue.md)  
    3. [AUTO FlushModeType](docs/7-Persistence-Context/7.2-AUTO-FlushModeType.md)  
    4. [Dirty-Checking](docs/7-Persistence-Context/7.3-Persistence-context-dirty-check.md)  
    -  _all the examples can be found in the module `advanced-topics` in the package: `io.costax.hibernatetunning.persistencecontext`_

8. **Batching**
    1. [Bash Updates Batch and Processing](advanced-topics/src/test/java/io/costax/hibernatetunning/persistencecontext/BatchProcessingTest.java)

9. **Cache**
    1. [1st and 2nd level and query caches](caches/Readme.md) 
        - 1st level (Persistence Context)
        - 2nd Level
        - Query cache
    2. [2nd level cache in JavaEE Application Server Example with infinispan (cache2ndee project)](cache2ndee)
    
10. **Bulk Operations** 
    1. [Bulk Operation examples](bulk-operations/Readme.md)

11. **Batching of write operations** 
    1. [Batching-of-write-operations examples](batching-of-write-operations/Readme.md)

12. **Concurrency** 
    1. [Concurrency About](concurrency/Readme.md)
    1. Isolation Issues
    2. Pessimistic Locking
        - `LockModeType.PESSIMISTIC_READ`
        - `LockModeType.PESSIMISTIC_WRITE`
    3. Optimistic-locking
        - `@Version`
        - Force Version Increment
    4. Timeout: `javax.persistence.lock.timeout`
    5. Deadlocks

13. **Fetching**
    1. [Query Hint Fetch Size](fetching/src/test/java/io/costax/queryhintfetchsize/QueryHintFetchSizeTest.java)
    2. [DTO Pagination](fetching/src/test/java/io/costax/queryhintfetchsize/DTOProjectionPaginationTest.java)
    2. [NativeSQL Synchronized Entity Class](fetching/src/test/java/io/costax/queryhintfetchsize/NativeSQLSynchronizedEntityClassTest.java)
    4. [Direct Fetching Entities](fetching/src/test/java/io/costax/fetchingentities/DirectFetchingTest.java)
    5. [Fetch Associations](fetching/src/test/java/io/costax/fetchingassociations/FetchAssociationsTest.java)
 
14. **Tips**
    - [How to map BLOBs and CLOBs with JPA and Hibernate](mapping-blobs-and-clobs/Readme.md)
    - [Soft delete](hibernate-tuning-howtos/hibernate-soft-delete/README.md)
    - [Enable Entity Listener for all entities](hibernate-tuning-howtos/entity-listener-for-all-entities/Readme.md)
    - [How Composite Identifier Automatically GeneratedValue](hibernate-tuning-howtos/how-composite-identifier-automatically-generatedvalue)
    - [How map localized data with jpa](hibernate-tuning-howtos/how-map-localized-data-with-jpa)
    - [How to Join unrelated Entities](hibernate-tuning-howtos/how-to-join-unrelated-entities)
    - [How to map a JPA entity to a View or SQL query using Hibernate](hibernate-tuning-howtos/how-to-map-a-jpa-entity-to-a-view-or-sql-query-using-hibernate/README.md)
    - [How to map an entity to multiple tables with SecondaryTable](hibernate-tuning-howtos/how-to-map-an-entity-to-multiple-tables-with-secondarytable)
    - [How to solve the postgresql cast operator](hibernate-tuning-howtos/how-to-solve-the-postgresql-cast-operator)
    - [How to use stored procedures](hibernate-tuning-howtos/how-to-use-stored-procedures)
    - [How to intercept entity changes with hibernate event listeners](hibernate-tuning-howtos/intercept-entity-changes-with-hibernate-event-listeners)
    - [How to load file properties lazy](hibernate-tuning-howtos/load-file-properties-lazy)
    - [How to map OneToOne fk in wrong side](hibernate-tuning-howtos/one-to-one-fk-in-wrong-side)
    - [How to use windows functions to optimise pagination](hibernate-tuning-howtos/query-pagination-using-window-functions)
        - Fix `HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!`
    - Enabling the hibernate.query.fail_on_pagination_over_collection_fetch configuration
        - Since Hibernate ORM 5.2.13, you can now enable the hibernate.query.fail_on_pagination_over_collection_fetch configuration property as follows: 
        - Using JPA property `<property name="hibernate.query.fail_on_pagination_over_collection_fetch" value="true"/>`
    - [Hibernate maven enhance plugin - enable Lazy Initialization](hibernate-tuning-howtos/hibernate-enhance-plugin)
        - Fix `N + 1` problem with `hibernate-enhance-plugin`
        - Impacts of enhance plugin on `@ManyToOne` relationship when this is marked with `fetch=LAZY`
    - [How to exclude distinct keyword from the generated JPQL and Criteria Queries](advanced-topics/src/test/java/io/costax/hibernatetunning/distincts/PassDistinctThroughHintTest.java)
    - [What are the principal JPA and Hibernate Hints](docs/hints.md)
    - [How to enable hibernate query logging in Wildfly](docs/1-Get-Started/hibernate-logging-in-wildfly.md)
    - [How to use bytecode enhancement dirty checking with hibernate-enhance-maven-plugin](docs/7-Persistence-Context/7.4-bytecode-enhancement-dirty-checking.md)
    - [JPA 2.1-Standardized schema generation and data loading](docs/JPA2.1_Standardized.md)
    - [How to Create An Entity Graph With Multiple Sub Graphs](advanced-topics-two/src/test/java/io/costax/hibernatetunnig/graphs/CreateAnEntityGraphWithMultipleSubGraphsTest.java)
    - [How to use NotFound hibernate annotation, Action to do when an element is not found on a association](advanced-topics-two/src/test/java/io/costax/hibernatetunnig/annotations/NotFoundAnnotationTest.java)
    - [How to Join @ManyToOne of multiple subtypes in multiple collections in the same entity](advanced-topics-two/src/test/java/io/costax/hibernatetunnig/mappings/JoinMultipleSubtypesTypesTest.java)
    - [How to implement a Hibernate ResultTransformer in JPA Criteria Query](advanced-topics-two/src/test/java/io/costax/hibernatetunnig/mappings/ResultTransformerInJPACriteriaQueriesTest.java)
    - [How logging Hibernate slow query](advanced-topics-two/src/test/java/io/costax/hibernatetunnig/sqlslow/ShowQueriesLogTest.java)
        - `<property name="hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS" value="25"/>`
    - [How to map OneToMany to a java.util.Map](hibernate-tuning-howtos/how-map-localized-data-with-jpa/src/main/java/io/costax/resourcebundle/Beer.java)
    - [Hibernate ToMany How to choice the right collection type](docs/Hibernate-toMany-in-what-collection.md)
    - [Localized Data – How to Map It With Hibernate](hibernate-tuning-howtos/how-map-localized-data-with-jpa)
    - [How to Fix OneToOne N+1 with manual enhance](instrumentation)
    - [How to keep Order in a Collections using javax.persistence.OrderColumn - JPA annotation](docs/OrderColumn.md)
    - [How to implement override value of the defined generator strategy](hibernate-tuning-howtos/override-generator-strategy)
    - [How to implement a custom application GenericGenerator for complex Identifiers](hibernate-tuning-howtos/custom-identifiers-generator)
    - [How to use Bean Validation In JPA Entities](bean-validation)
    - [Difference between @NotNull and @Column(nullable = false)](bean-validation)
    - [Hibernate 5 naming strategies](hibernate-5-naming-strategies)
    - [Xa Transactions (2-phase-commit)](xa-transactions-2-phase-commit)
    - [How to map generated values](how-to-use-a-sequence-generator-for-a-non-id-field)
    - [When and why JPA entities should implement the Serializable interface](docs/When-entities-should-implement-theSerializable-interface.md)
    - [The best way to use Java Records With JPA](the-best-way-to-use-java-records-with-jpa)