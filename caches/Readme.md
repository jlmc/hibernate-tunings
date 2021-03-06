# Caching 


caching is another way to improve your read operations, the general idea is to keep a copy of the data in local memory, and avoid the database round trip.

Hibernate provide us two well know cache level. The *1st level* and the *2nd level*

# 1 st Level Cache

- works at the Hibernate session level, thefore, if we have two diferent sessions we have also two 1sts caches, one for each hibernate session  

- also know as Persistence context

- activated by default

- contains all entities used during the life cicle os the hibernate session


# 2nd Level cache

- Also keep entities, but is independent of the hibernate session
- All the Hibernate sessions share this cache
- session independent
- requires additional configurations


## Query Cache

- The first and second cache levels work with entities. However this Query cache works query results. 
- Query cache is a proprietary feature of hibernate.
- The purpose of the query cache is to cached results, (ids and scalar values for example). 
- Session independent.
- Requires additional configurations.


# 2nd level 

store entities, but in deference to the 1en is independent from the session. 
That means that a Session A can put some instance in the cache, and a other Session B can read it from the cache

- Transparent usage
- Is part of the JPA specification, but the provider implementation don't need to provide it, allow us to choice the implementation that we need to use (eg. ECache or infinispan)
- Hibernate can provide some internal cache but it should not be used in production. it should be used only for test proposes
- If we are using hibernate with the aplication server wildfly or jboss then we should use infinispan
    

- Needs to be activated
    
    - on the persistence.xml or EntityManagerFactory 

        ```xml
        <!-- enable selective 2nd level cache in persistence.xml -->
        <shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>
        ```


- We need to provide the **shared-cache-mode** when we actived the 2nd level cache, this configuration defines with entities should be cached. We can choose 4 different modes: 

  - **ALL**
    
    cache all entities, not recommended 
  
  - **NONE** 
  
    cache no entities, not recommended
  
  - **ENABLE_SELECTIVE**
  
    cache needs to be activated for specific entities
    the most commonly used mode is enable_selected, which allows you to define exactly which entities should be cached. this makes much more sense than to cache everything, because we do not get cache for free, it requires memory to store the entities and CPU time to manager the cache, so to use it we must have absolutely sure that the benefits of having a cached entity are higher than directly accessing the Database. 
    this is only the case when the entity is only read without being changed.
    Entities that are not read from the cache only consumes memory, and if it is necessary to update the caches it will consumes time.
  
  - **DISABLE_SELECTIVE**
  
     cache can be deactivated for specific entities


### When does Hibernate use the 2nd Level Cache?

- for the EntityManager.find(Class, id)
- To traverse relationships
    ```
        Project a = em.find(Project.class, 1L);
        
        // use a getter method to get access related entities
        a.getDevelopers();
    ```
- But not for any query! (JPQL, Criteria, or NativeQuery)

### How is the entity stored in the cache?

- Stores only the entity properties, not the entity itself

    - 1 -> [,"Joshua", "Bloch", 0, , , ... ]  
    
    - Id and a array with values of the properties
    
- Does not store the relations between entities

    - this feacture can be activeted with the annotation: @org.hibernate.annotations.Cache 


### Examples


In persistence.xml

```xml

<shared-cache-mode>ENABLE_SELECTIVE</shared-cache-mode>

<property name="hibernate.cache.region.factory_class" value="org.hibernate.cache.ehcache.EhCacheRegionFactory"/>
```

In The entity:

we can use the annotation `@javax.persistence.Cacheable` defined in JPA specification, that activates the cache for the current entity

```java
@Entity
@javax.persistence.Cacheable 
public class Project {
}
```

Or 
If we are using hibernate was JPA provider, we can use use the annotation: `@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL) ` with does the same of the previous annotation
But can be used to find more configurations like CacheConcurrencyStrategy

```java
@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Project {
}
```

Please check the testcase **TestSecondLevelCache**

###### What about the caching of relationship?

- By default Hibernate does not cache the relationship between two entities. Even if the two entities are already in the cache.

- This means that even if two related entities have already ben injected in the cache: it is necessary to execute a query to know that that to entities are related.

- This behavior is almost certainly not what we expect to have when we activate the 2 level cache.

- Based in the JPA specification relationships are not cached.

- Fortunately we can improve this behavior, by annotating the entity relationship with the Hibernate specific annotation.
    
```
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
```


### More about Hibernate cache configurations

Lets get a let bit deeper in the Hibernate cache configurations. 
There are two very important thing that we can define: 


- **Cache Retrieve Mode**

    - Defines How Hibernate read entities from the cache
    
    - We can choice two differents modes to get Entities from the cache
    
        - **CacheRetrieveMode.USE (default)** 
            
            Read entities from the cache, therefore, the Hibernate will check if the 2nl cache contains the entity cache and getting from there.
            
        - **CacheRetrieveMode.BYPASS**
            
            Read entities from the database. This mode is indicated when we know that the entities are not updated. In this case it is better to get the entity directly from the database.
            Reasons for the entities are unupdated could be for example: external applications, batch jobs ou update executed by query that that are not synchronized with the entities.
            
        _We can find a example of use in the testcase_ **TestRetrieveAndStoreMode#testRetrieveMode**
    
- **Cache Store Mode**

    - Define How entities are write entities to the cache
    
    - We can choice from 3 different types:
    
        - **CacheStoreMode.USE (default)**
        
          Entities get added or updated during transaction commit.
          No force update for read operation.
          
        - **CacheStoreMode.BYPASS**
        
          Entities get updated during transaction commit. 
          Only update the existing entities.
          New entities will not be add to the cache, this can be usefully during a batch process when for example we what to add a huge number of entities but don't what to read them.
          
        - **CacheStoreMode.REFRESH** 
          
          This Mode is very similar to the **CacheStoreMode.USE**. 
          On Top of added new or updated existing entities hibernate will perform a query for each entity with is requested fro the cache and update it.
          Entities get added or updated during transaction commit.
          Force update for read operations.

        _We can find a example of use in the testcase_ **TestRetrieveAndStoreMode#testStoreMode**





### Concurrency Strategy

The 2nl cache is used by multiple session at the same time, so, we need to think about concurrency.
 

- JPA: we can only define Concurrency Strategy globally (for all the application) 

- Hibernate: We can specify a definition for each entity using the annotation:
    
```java
@org.hibernate.annotations.Cache()
```

We can use the following types:

+ READ-ONLY: 

    Very god feet to entities that never change, but very restritive for all the other use cases.
    

+ Read-write
  
  Do not use for serializable transaction isolation

+ Nonstrict-read-write

  No strict transaction isolation

+ Transactional
  
  this mode turns out to be the most advisable to be used
  Requires JTA
  E.g. Infinispan, EHCache
  
  

    
    
### Programmatic cache management via: javax.persistence.Cache interface

Entities can be removed from cache


```java
// remove one specific entity
cache.evict(Project.class, 1L);
// remove all entities of a specific type
cache.evict(Project.class);
// remove all entities
cache.evictAll();
```

This can be very useful to remove outdated entities, The Batch jobs are a good example to force the clean of the cache:
We can find a exanple of use the test case: **TestCacheManagement**






---

# Hibernate Query Cache


- Hibernate specific

  - Only supported by Hibernate and not portable for others JPA implementations 
  - 1st and 2nd level of cache both cache entities and con not be used for queries


- Stores query results session independently

  - The query cache caches queries results in a session independent way 

- Needs to be activated in persistence.xml
 
  - Is not activated by default 

```xml
<properties>
    <property name="hibernate.cache.use_query_cache" value="true"/>
</properties>
```

- Needs to be explicitly activated for a query

  We need also to activate the cache to each query we want do cache the results that can be do by using the following two methods

```
org.hibernate.Query.setCacheable(true)
```

or 

```
@NamedQuery(name = “…”, query = “…”,
    hints = QueryHint( name="org.hibernate.cacheable", value="true"))
```

Having to enable caching for some queries may seem strange at first, but there a good reason for it, we shouldn't use case cache for mostly of our queries:

The result of a Query dependents not only of the Query it self but also on the query values of its Parameters, in other words, the value of the parameters causes the results of the queries to be different. Therefore, Hibernate must store the queries result values for each set of parameter values. That is the reason for the Query Caching does not make sense to most of queries.

Hibernate may have to handle many combinations of parameter values which may require a lot of processing and memory. I do this
to be able to have a possible benefit that may not be advantageous, because many of the entries of the cache may have already been removed when suggesting the need to be alloys a second time.

We should cache a Query results if and only if we perform a Query with the exact same parameter values very often. E.G. Configuration Data, Reports that are the same for lots of users. 

The Query cache only stores the references to the entities, this means that hibernate have to fetch the properties of each entity. this is the reason it only makes sense to use cache caches in conjunction with the 2nd active cache level, because in this way hibernate can fetch those values in the second level cache, otherwise Hibernate have to perform a query that get the entity from the database.


We can find examples of Use in the test case: `TestQueryCache`



## Caching NaturalId

If the second-level cache is enabled, Hibernate can avoid executing the second query by loading the entity directly from the cache. 
Hibernate can also cache the natural identifier (e.g. **@NaturalIdCache**) associated with a given entity identifier, therefore preventing the first query as well.


## Summary

- Stores query results for a query and its parameters,
- [„FROM Author WHERE id=?“, 1]  [1]
- Stores only entity references or scalars
- Always use together with 2nd Level Cache, configuration has to fit to each other