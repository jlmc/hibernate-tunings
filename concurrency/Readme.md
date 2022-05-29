# Concurrency

- Pessimistic lock
- Pessimistic lock
- Level of insulation

## Introduction

- Databases try to isolate concurrent transactions

    The databases try to use isolated transactions as a way to eliminate potential Concurrency issues.
    The isolation mechanisms however are specific of the database engine.
    We should/must read the documentation of each database engine to understand how it controls the mechanisms of isolation, we must understand the Concurrency of the database engine we are using.
    Only by understanding the DBM (database mechanism) Concurrency can we understand how hibernate's Concurrency strategies work.

- Hibernate inherits concurrency control from the database
    
    Hibernate inherits concurrency control from the database engine, for example hibernate uses base date features to block specific records

## Isolation Issues

The isolation of concurrent transactions it is use to serve different issues. Some examples of it use are the following:

1. Lost update
	- use when 2 concurrent transactions update the same record.
	- 2 transactions update record without isolation
	- without any concurrency control in place the secound transaction the rollback of the secound Tansaction would so remove the commited changed of the first Transaction. 
	- Rollback of transaction 2 removes changes of transaction 1

2. Dirty read (https://en.wikipedia.org/wiki/Isolation_(database_systems)#Dirty_reads)
	- Transaction 1 reads uncommitted changes of transaction 2.
	- A dirty read (aka uncommitted dependency) occurs when a transaction is allowed to read data from a row that has been modified by another running transaction and not yet committed.
	Dirty reads work similarly to non-repeatable reads; however, the second transaction would not need to be committed for the first query to return a different result. The only thing that may be prevented in the READ UNCOMMITTED isolation level is updates appearing out of order in the results; that is, earlier updates will always appear in a result set before later updates.

3. Unrepeatable read
	- When a Transaction reads the same record twice and gets different states, because another transaction committed and updated that record.

	- Special case: Last commit wins
		- Transaction 2 overwrites changes of transaction 1

4. Phantom read
	- 2nd execution of query returns data which wasn‘t visible before.
	- when a secound execution o.f the 2nd execution of query returns different data of the first execution. because other transaction commit some changes 

---

#### Levels

 Defined by ANSI SQL standard

- Read uncommitted isolation
	- Allows dirty reads but not lost updates

- Read committed isolation
	- Allows unrepeatable reads but not dirty reads
	- JPA default isolation level
	- There are almost no applications that need dirty reads, so this type of level turns out to be the best choice and the best choice

- Repeatable read isolation
	- Allows phantom reads but not unrepeatable or dirty reads

- Serializable isolation
	- Emulates serial execution of transactions

---

### Locking 

- Pessimistic locking
	- Detects concurrent modifications during commit
	- Requires no additional database locks
	
- Pessimistic locking
	- Locks records in the database to prevent concurrent modifications
	
	
## optimistic-locking

The name optimistic locking is a little bit misleading because it does not use locks to avoid concurrent updates. 
This allows a much better scalability than the pessimistic locking which uses row locks to prevent concurrent updates.

 optimistic locking in JPA and Hibernate use an automatic version check, witch detect conflicts at **commit time** or **flush time**.
 
The commit with conflicts are rejected, this mends that the **first commit wins**.

### How to use

- Defined in JPA spec.
- Need to be activated for each Entity that need to be controlled.
- Requires an additional column (int, short, long).

    ```
       @Version
       private int version;
    ```
    
- Hibernate update the version for all dirty entities that entry to flush.
- we should not worry if we exceed the number of possibilities for the version, because if we exclude the number of updates the version will start again at 0.
- We can also use the Timestamp type or Date, but in this case we should be prepared for some additional problems or limitations.


- Version gets checked in the SQL update statement

    ```sql
    UPDATE Author 
        SET first_name = ?, last_name = ?, version = ?
        WHERE id = ? AND version = ?
    ```
    
- Hibernate throws OptimisticLockException if no records were update.


###### Using Timestamps

If already have a column that is modifier in each update operation we can also use it as version control column.

    ```java
    @Version
    private Date modDate;
    ```
    
But that can bring us some problems:

- Updates in same milliseconds are not detected
    - if two concurrent transaction update the same record in the exactly the same millisecond. Hibernate is not able to detected that.
    
- Retrieving the current time in milliseconds
    - The Value is give us by the JVM, and it is very hard to get the current value in milliseconds by the JVM, because the JVM don't give us the accuracy.
    - JVM accuracy  
    - Different timestamps in clustered environment
    
    
One option to improve the previous problem is to retrieve the current timestamp from the database.

    ```java
    @Version 
    @Type(type = "dbtimestamp")
    private Date modDate;
    
    ```
    
- This is a Hibernate  specific feature.
- Requires an additional query, witch makes this approach slower that use for example a int.
- Not supported by all dialects
   
```
15:24:25,469 DEBUG [org.hibernate.SQL] - 
    select
        now()
15:24:25,475 DEBUG [org.hibernate.SQL] - 
    update
        multimedia.Publisher 
    set
        mod_date=?,
        name=? 
    where
        id=? 
        and mod_date=?
15:24:25,476 TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [TIMESTAMP] - [2019-03-31 15:24:25.483134]
15:24:25,476 TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [2] as [VARCHAR] - [Orealy -- updated]
15:24:25,476 TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [3] as [INTEGER] - [1]
15:24:25,476 TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [4] as [TIMESTAMP] - [2019-03-31 14:13:41.078041]
15:24:25,478 DEBUG [org.hibernate.engine.jdbc.batch.internal.BatchingBatch] - Executing batch size: 1
``` 

###### Force Version Increment

optimistic-locking is easy to understand if we use just one entity, but it could not be good enough we have concurrent modification in relationships.

consider the following scenario flow:

1. Transaction 1 adds a related entity to a root entity
2. Transaction 2 gets all related entities and performs update
3. Root entity is not changed -> hibernate can't detect conflict.

The operation made by Transaction2 may be invalid, and hibernate can't detected.
We can a void this problem by increment the version of the root entity in two transactions.


```java
Author a = em.find(Author.class, 2L, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

```

and with Query 


```java
 Author a = em.createQuery(
                "select distinct a from Author a left join fetch a.books where a.id = :id", Author.class)
                .setLockMode(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
                .setParameter("id", 1L)
                 .setHint("hibernate.query.passDistinctThrough", false)
                .getSingleResult();
```


---

## Pessimistic lock

Hibernate and JPA also support pessimistic locking, but you should only use it if you really need it. The version-based optimistic locking provides much better scalability.

- Has to be explicitly activated pessimistic lock for a particular query
    - Hibernate will acquire a row locking database until the end of the current transaction.
- Locks data for the duration of the current transaction.

    - This has the advantage of we can execute our business operation without any type of conflicts at commit or flush time.
    
    - But also provides more waiting time than an optimistic lock 

- Uses row locks on the database
    - Lock implementations are database specific
    - Supported lock modes might differ between databases
- Can be used together with optimistic locking


### LockModeType

2 different LockModeType

- PESSIMISTIC_READ
    - Other transactions can read but not write
    - Guarantees repeatable reads

- PESSIMISTIC_WRITE
    - Other transaction can’t read or write
    - Serializes data access
    - serialize all transactions that need to access to the current row
    - should only be used if we update the record


#### Specify LockModeType

- Query interface

```java
Query q = em.createQuery("SELECT a FROM Author a WHERE id = 1");
q.setLockMode(LockModeType.PESSIMISTIC_READ);
```


- EntityManager.find

```java
em.find(Author.class, 1L, LockModeType.PESSIMISTIC_READ);
```

#### Timeout

- Time the database waits to acquire the lock

- Defined in milliseconds with a query hint

```java
Map<String, Object> hints = new HashMap<String, Object>();
hints.put("javax.persistence.lock.timeout", 3000);
```

- Needs to be supported by database and Hibernate dialect
- Some dialects transform 0 into a NOWAIT clause

PostgresSQl unfortunately does not support queries specific locking timeout
But it transform 0 into a NOWAIT


#### Deadlocks

- Exclusive locks can create deadlocks
- Handled by the database
    - Transaction gets terminated after timeout
    - Deadlock detection finds deadlock and aborts on transaction
- Ordering of update statements reduces probability
```xml
<property name="hibernate.order_updates" value="true"/>
```


#### Summary

* Pessimistic locks use row locks on the database
    * Depends on database support
    * **PESSIMISTIC_READ** blocks updates
    * **PESSIMISTIC_WRITE** block all access
* Lock timeout defines how long the database tries to acquire the log
