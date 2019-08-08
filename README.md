# Performance Note Java Persistence and Hibernate

## Performance Facts

“More than half of application performance bottlenecks originate in the database” - http://www.appdynamics.com/database/


“Like us, our users place a lot of value in speed — that's why we've decided to take site speed into account in our search rankings.”
Google Ranking - https://webmasters.googleblog.com/2010/04/using-site-speed-in-web-search-ranking.html


“It has been reported that every 100ms of latency costs Amazon 1% of profit.”
http://radar.oreilly.com/2008/08/radar-theme-web-ops.html

## JPA vs Hibernate

* JPA is only a specification. It describes the interfaces that the client operates with and the standard object-relational mapping metadata (annotations, XML).

* Although it implements the JPA specification, Hibernate retains its native API for both backward compatibility and to accommodate non-standard features.




## HOW TO RUN THIS EXAMPLES


###### 1. First off all, we need to create a Postgres Data Base with the name postgresdemos in the port 5432. For example we can use Docker to create that resource:

```bash
docker run --name postgresdemos \
    -p 5432:5432 \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    -e POSTGRES_DB=postgresdemos \
    -v $(pwd)"/postgresdemos:/var/lib/postgresql/data" -d postgres:11.4
```

###### 2. Run the migrations:

```bash
cd database-migrations
    
mvn flyway:migrate -Dflyway.configFiles=local.conf
```

###### 3. we are able to run the examples.

**Very important note:**

This project is builder at the moment using Java JDK  11, so to compile the project we must execute the following command.

```bash
mvn clean install -Dmaven.test.skip=true -X -Dnet.bytebuddy.experimental=true
```

or, on the limit to compile a single module: 

```bash
mvn clean install -rf :hibernate-open-source-custom-types-project -Dmaven.test.skip=true -X -Dnet.bytebuddy.experimental=true
```

otherwise, if you are using java JDK 8 then to run this project you sgould remove the following dependencies from the root main pom file:

```xml
        <!-- using with JDK 11 -->
        <dependency>
            <groupId>javax.xml.bind</groupId>
            <artifactId>jaxb-api</artifactId>
            <version>2.3.0</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jaxb</groupId>
            <artifactId>jaxb-runtime</artifactId>
            <version>2.3.0.1</version>
        </dependency>
``` 

And then you can simple execute the maven command:

```bash
    mvn clean install -DskipTests
```


## Agenda

**1 - Get-Started**

 - 1.1. [logging-sql-statements]
   - [Typical causes of performance problems](docs/1-Get-Started/1 and 2--loggers-notas.md)
   - [Why We Need Logging](docs/1-Get-Started/README.md)
   - [logging-sql-statements-examples](logging-sql-statements/README.md)
    
 - 1.2. [schema-management](docs/1-Get-Started/1.4-schema-manager.md)

**2 - Connections**    

 - 2.1. [connection-manager](docs/2-Connections/Readme.md)
 - 2.2. connection-lifecycle
    
**3 - Types**
    
 - 3.1 - [jpa-and-hibernate-types](types/jpa-and-hibernate-types.md)
 - 3.2 - [custom-hibernate-types](types/custom-hibernate-types/custom-hibernate-type.md)
 - 3.3 - [open source hibernate types](types/hibernate-open-source-custom-types-project/README.md)


4 - [Identifiers](docs/4-Identifiers/GeneratingPrimaryKeys.md)

 - 4.1 - [Identifier](docs/4-Identifiers/4.1%20-%20Identifier.md)
 - 4.2 - [Hibernate Sequence Optimizers](docs/4-Identifiers/4.2%20-%20hibernate-sequence-optimizers.md)


**5 - Relationships**    
    
    5.1 - equals-and-hash (EqualsConsistentCheckTest)
    5.2 - one-to-many-and-many-to-one (OneToManyAndManyToOneTest)
    5.3 - one-to-one
    5.5 - Many To Many
    
**6 - Inheritance**     
    
    6.1 - SingleTable (SingleTableTest)
    6.2 - DiscriminatorColumn
    6.3 - JoinedInheritance (JoinTableTest)
    
       
**7 - Persistence Context and Flushing** 

7.1 - [PersistenceContext_Flushing](docs/7-Persistence-Context/7.0-PersistenceContext.md)  
7.2 - [ActionQueue](docs/7-Persistence-Context/7.1-ActionQueue.md)  
7.3 - [AUTO FlushModeType](docs/7-Persistence-Context/7.2-AUTO-FlushModeType.md)  
7.4 - [Dirty-Checking](docs/7-Persistence-Context/7.3-Persistence-context-dirty-check.md)  
    
all the examples can be found in the module advanced-topics in the package: io.costax.hibernatetunning.persistencecontext


**8 - Batching**

**8.1 - bash-updates** [BatchProcessingTest](advanced-topics/src/test/java/io/costax/hibernatetunning/persistencecontext/BatchProcessingTest.java)

**9 - Cache** [2nd level and query cache](caches/Readme.md) 

**10 - Bulk Operations** [Bulk Operation examples](bulk-operations/Readme.md)

**11 - Batching-of-write-operations** [batching-of-write-operations](batching-of-write-operations/Readme.md)

**12 - Concurrency** [Concurrency](concurrency/Readme.md)


**13 - Fetching**

 - 13.1 - [Query Hint Fetch Size](fetching/src/test/java/io/costax/queryhintfetchsize/QueryHintFetchSizeTest.java)
 - 13.2 - [DTO Pagination](fetching/src/test/java/io/costax/queryhintfetchsize/DTOProjectionPaginationTest.java)
 - 13.3 - [NativeSQL Synchronized Entity Class](fetching/src/test/java/io/costax/queryhintfetchsize/NativeSQLSynchronizedEntityClassTest.java)
 
 - 13.4 - [Direct Fetching Entities](fetching/src/test/java/io/costax/fetchingentities/DirectFetchingTest.java)
 - 13.5 - [Fetch Associations](fetching/src/test/java/io/costax/fetchingassociations/FetchAssociationsTest.java)
 



## Extra-tunnings
    
> A - query-pagination-using-window-functions, How to resolve the HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!     

> A - How to detect HHH000104 issues with hibernate.query.fail_on_pagination_over_collection_fetch

> B - Fix N + 1 probleam with  hibernate-enhance-plugin  

> C - override-generator-strategy: override the identity and sequence generation strategy
