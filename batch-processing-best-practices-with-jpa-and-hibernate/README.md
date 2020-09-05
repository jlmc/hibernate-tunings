# Batch Processing Best Practices With JPA And Hibernate


## How to show queries log in PostgreSQL?

You have to config the PostgreSQL configuration file postgresql.conf.

On Debian-based systems it’s located in `/etc/postgresql/9.3/main/` (replace 9.3 with your version of PostgreSQL)

On Red Hat-based systems in `/var/lib/pgsql/data/`.
If you still can’t find it, then just type `$locate postgresql.conf` in terminal, or execute the following SQL query:

```
SHOW config_file;
```

1. Then you need to alter these parameters inside PostgreSQL configuration file.
```
log_statement = 'all'
log_directory = 'pg_log'
log_filename = 'postgresql-%Y-%m-%d_%H%M%S.log'
logging_collector = on
log_min_error_statement = error

```

On older versions of PostgreSQL prior to 8.0, replace 'all' with 'true' for the log_statement:

`log_statement = 'true'`

2. Then restart the server

Run this command:

`sudo /etc/init.d/postgresql restart`

or this

`sudo service postgresql restart`

The content of all queries to the server should now appear in the log.

3. See the log

The location of the log file will depend on the configuration.

On Debian-based systems the default is `/var/log/postgresql/postgresql-9.3-main.log` (replace 9.3 with your version of PostgreSQL).
On Red Hat-based systems it is located in `/var/lib/pgsql/data/pg_log/`.
Using TablePlus, you can enable the console log via the GUI and see all the queries.

To do that, click on the console log button near the top right panel, or use the shortcut key Cmd + Shift + C.


## JDBC Statement batching

- Oracle implements JDBC API, but only for the prepared Statement.
- For `Statement` and `CallableStatement`, the Oracle JDBC Driver doesn't actually support Batching, each statement is being executed separately.
- Basically oracle driver will send one statement after the other.

- By default, the MySQL JDBC Driver doesn't send the batched statement in a single request.
- The `rewriteBatchedStatements` connection property includes all batched statements into a single StringBuffer.


#### Example 
```java
        try (PreparedStatement matchStatement = connection.prepareStatement("""
                insert into matches (id, at, hometeam, awayteam, version) 
                values (?, ?, ?, ?, ?)
                """)) {

            matchStatement.setInt(1, 1);
            matchStatement.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-15")));
            matchStatement.setString(3, "Benfica");
            matchStatement.setString(4, "Gil Vicente");
            matchStatement.setInt(5, 0);
            matchStatement.addBatch();

            matchStatement.setInt(1, 2);
            matchStatement.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-20")));
            matchStatement.setString(3, "Benfica");
            matchStatement.setString(4, "Real Madrid");
            matchStatement.setInt(5, 0);
            matchStatement.addBatch();

            final int[] updateCounts = matchStatement.executeBatch();

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
        }
```

- The JDBC API is not very popular, because it is very verbose.
- It is not very developer friendly.

### Advantages

- SQL Injection prevention
- Better performance

--- 

## Hibernate automatic JDBC batching

- SessionFactory level

```xml
<property name="hibernate.jdbc.batch_size" value="10"/>
```


- Session level

```java
EntityManager em = ...
em.unwrap(Session.class).setJdbcBatchSize(1234);
```


- Hibernate allows you to switch automatically from non-batching to batching `PreparedStatement`(s) without no data access code changes.

- Many other data access frameworks require you to change the access code in order to enable JDBC batching.


## Identify columns and JDBC Batching

- If the primary key table use Identity columns (serial), then hibernate would disable batched inserts.

- Once an entity becomes managed, the Persistence context needs to know the entity identifier to construct the first level cache entry key, and, for the identity columns the only way to find the primary key value is to execute the insert statement!!!

- This restriction does not apply to update and delete statements witch can still benefit from JDBC batching even if the entity uses the identity strategy.

---

## Cascading Parent-child entity state transition

Please see the test example `CascadingParentChildEntityStateTransitions` class.

Even if we have batching activated in Hibernate, but we are using the cascading flush strategy like for example the following example:

```java
package io.github.jlmc.batching;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "matches")
@SequenceGenerator(
        name="matches_generator",
        sequenceName = "matches_seq", initialValue = 10, allocationSize = 10)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matches_generator")
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    private LocalDate at;

    private String homeTeam;

    private String awayTeam;

    @OneToMany(
            mappedBy = "match",
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            cascade = CascadeType.ALL
           )
    private Set<MatchEvent> events = new HashSet<>();

    @Version
    private int version;

   // ... 
}
```

```java
package io.github.jlmc.batching;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "match_events")
@SequenceGenerator(
        name="match_events_generator",
        sequenceName = "match_events_seq", initialValue = 100, allocationSize = 20)
public class MatchEvent {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE, 
        generator = "match_events_generator")
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    private Integer minute;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, updatable = false)
    private Match match;
    
    // ...

}

```

- The `Match` entity is the parent entity, the aggregate root. Witch was a child collection `events`. This is a very common pattern is the real world.
- This pattern makes very since, because it allows us to:
  - Save the Match instance and automatically also save all the child MatchEvent.
  - Update the Match will update also the child MatchEvents
  - Remove/Delete a Match will also Remove/Delete the associated MatchEvent. 


- If we execute the following code snippet:

```
    @Test
    void createMatchWithEvents() {

        context.doInTx(em -> {

            for (int i = 1; i <= 3; i++) {

                final Match match =
                        Match.of(LocalDate.parse("2020-08-10"), "home-" + i, "away-" + i)
                                .addEvent(MatchEvent.of(1, "Start Match : " + i));

                em.persist(match);
            }
        });

    }
``` 


- The default hibernate behavior will be:
```sql
insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-1', 'home-1', 0, 10)
insert into match_events (description, match_id, minute, id) values ('Start Match : 1', 10, 1, 100)

insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-2', 'home-2', 0, 11);
insert into match_events (description, match_id, minute, id) values ('Start Match : 2', 11, 1, 101)

insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-3', 'home-3', 0, 12)
insert into match_events (description, match_id, minute, id) values ('Start Match : 3', 12, 1, 102)
```

- Instead of seeing only one insert statement for the `insert into matches` and other to the `insert into match_events` we can have right after each `insert into matches` the insert into match_events that are associated with that match.
- This means that the batching is not working by default when we use the Cascade flush strategy.

- To Active the batching when we use the Cascade flush strategy, hibernate give us the one possible configuration:

```xml
<property name="hibernate.order_inserts" value="true"/>
```

- After active the previous configuration:
  - Hibernate will execute the insert the parent firsts and only then the all the child inserts will be executed.
  - Hibernate will optimise the order using batching, mends that hibernate will arrange the statement's execution plan in way we get a better benefit from batching. 

```sql
insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-1', 'home-1', 0, 10)
insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-2', 'home-2', 0, 11)
insert into matches (at, awayTeam, homeTeam, version, id) values ('2020-08-10T00:00:00.000+0100', 'away-3', 'home-3', 0, 12)

insert into match_events (description, match_id, minute, id) values ('Start Match : 1', 10, 1, 100)
insert into match_events (description, match_id, minute, id) values ('Start Match : 2', 11, 1, 101)
insert into match_events (description, match_id, minute, id) values ('Start Match : 3', 12, 1, 102)
```


- For the update batching the default behavior is also the same, first hibernate will execute updates for the aggregate entity and only after will execute the updates for the child entities.
- To achieve a better batching execution plan hibernate give us also a configuration property:

```xml
<property name="hibernate.order_updates" value="true"/>
``` 

### Batching versioned data

- Prior to **Hibernate 5**, JDBC batching was disabled for versioned entities (e.g., `@Version`) during `update` and `delete` operations. This limitation was due to some JDBC Driver inability of correctly return the update count of the affected table rows when using JDBC batching.
- No matter if you enable the `hibernate.jdbc.batch_versioned_data configuration` to the value of `true` because hibernate will ignore that configuration and will disable.

- **If the JDBC Driver supports mixing optimistic locking with JDBC batching**, then you can should set the `hibernate.jdbc.batch_versioned_data configuration` to the value of `true`.


> Now days

- Since Hibernate 5, the `hibernate.jdbc.batch_versioned_data` configuration property is enabled by default, and it’s only deactivated when using a pre-12c Oracle dialect (e.g. Oracle 8i, Oracle 9i, Oracle 10g).

- Because the Oracle 12c JDBC driver manages to return the actual update count even when using batching, the Oracle12cDialect sets the `hibernate.jdbc.batch_versioned_data` property to true.

- For Hibernate 3 and 4, the `hibernate.jdbc.batch_versioned_data` should be enabled if the JDBC driver supports this feature.


### Batching DELETE statements

- Once the `hibernate.jdbc.batch_size` configuration property is set up, JDBC batching applies to SQL `DELETE` statements too.

- Currently, there is no delete statement batch ordering feature, which should be addressed by the `HHH-10483` issue.

- On the other hand, the `hibernate.jdbc.batch_versioned_data` property applies to `DELETE` statement batching, just like it was the case with `UPDATE` statement batching.

#### Cascading DELETE statements – Workaround 1

1. Remove all the child entities first.
2. Execute an `EntityManager#flush()`
3. Remove all the parent entities.

Example:

```java
    @Test
    void Cascading_DELETE_statements_Workaround_1() {
        cx.doInTx(em -> {
            final List<Match> matches =
                    em.createQuery(
                            """
                            select m from Match m
                           """, Match.class)
                            .getResultList();

            for (Match post : matches) {
                for (Iterator<MatchEvent> eventIterator = post.getEvents().iterator(); eventIterator.hasNext(); ) {
                    MatchEvent event = eventIterator.next();
                    event.setMatch(null);
                    eventIterator.remove();
                }
            }

            em.flush();

            matches.forEach(em::remove);
        });
    }
```

#### Cascading DELETE statements – Workaround 2

- A more efficient alternative is to execute a bulk `DELETE` statement instead.
- So, the MatchEvent collection will be modified to avoid cascading the remove entity state transition by replacing `CascadeType.ALL` and `orphanRemoval` settings.

1. Set the cascade strategy without the CascadeType.DELETE
2. Execute a sql or JPQL delete for all the child entities.
3. Execute an `EntityManager#flush()`.
3. Remove all the parent entities.


Example
```java
@OneToMany(
    mappedBy = "match",
    cascade = {
        CascadeType.PERSIST, CascadeType.MERGE
    }
)
private List<MatchEvent> events = new ArrayList<>();
```

```java
    @Test
    void cascading_DELETE_statements_Workaround_2() {
        cx.doInTx(em -> {

            final List<Match> matches =
                    em.createQuery(
                            """
                            select m from Match m
                           """, Match.class)
                            .getResultList();

           em.createQuery(
                   """
                   delete from MatchEvent me
                   where me.match in :matches
                   """
               )
               .setParameter("matches", matches)
               .executeUpdate();

            em.flush();

            matches.forEach(em::remove);
        });
    }
```

#### Cascading DELETE statements – Workaround 3

- The most efficient approach is to rely on DDL cascading.

- For this purpose, the `match_events` table should be modified so that the `match_id` Foreign Key constraint defines a `DELETE CASCADE` directive.

- This way, the deletion operation can be reduced to simply removing the `match` table rows.

- This option can be very dangerous, because if we delete by mistake a record we can also lose a lot of relevant data in the database. In the most part of the cases having just a constraint without the cascade it is the better option.

```sql
ALTER TABLE match_events
ADD CONSTRAINT fk_match_events_match
FOREIGN KEY (match_id) REFERENCES matches
ON DELETE CASCADE
```

```java
List<Match> matches = entityManager.createQuery("""
select p
from Match p
""", Match.class).getResultList();

matches.forEach(entityManager::remove);
```

---

## Handling JDBC batch failures


When we are executing all the statements manually, it also very easy and common something does wrong.  

Every statement is executed after the other, and the one that failed is the last one that has been executed.

```java
package io.github.jlmc.batching.jdbc;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JpaTest(persistenceUnit = "it")
public class HandlingJDBCBatchFailuresTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlingJDBCBatchFailuresTest.class);

    @PersistenceContext
    EntityManager em;
    private Connection connection;

    @BeforeEach
    void setUp() {
        this.connection = em.unwrap(SessionImpl.class).connection();
    }

    @Test
    void handlingJDBCBatchFailures() throws SQLException {

        try (PreparedStatement st = connection.prepareStatement(
                """
                        insert into matches (id, at, hometeam, awayteam, version) 
                        values (?, ?, ?, ?, ?)
                        """)) {

            for (int i = 1; i <= 3; i++) {

                st.setInt(1, i % 2);
                st.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-20")));
                st.setString(3, "Home " + i);
                st.setString(4, "Away " + i);
                st.setInt(5, 0);

                st.addBatch();
            }

            st.executeBatch();

            connection.commit();

        } catch (BatchUpdateException e) {
              LOGGER.info("Batch has managed to process {} entities", e.getUpdateCounts().length);
        }
    }
}

```

- The `BatchUpdateException#getUpdateCounts()` give us the number of batch parameters that have been successfully processed.


## Batching merge vs. update – saving entity changes

- Basically we have two options.

Option-1: To save detached entity state, we can either use the JPA EntityManager merge method:
    ```
    for (Match match : matchs) {
        entityManager.merge(match);
    }
    ```
  - For each item may be executed two queries, 
    - 1 select, if the entity instance is detached.
    - 1 update to statement, if and only if any entity property value the has changes (is dirty)
  - So, we may have lots of selects, witch is not a god idea.


Option-2: Or the Hibernate Session update method:

```
Session session = entityManager.unwrap(Session.class);
for (Match match : matchs) {
    session.update(match);
}
```

  - For each item one queries:
    - Only 1 update to statement, even if the entity has not changed (is dirty), it is a force update


---

## PostgreSQL batch statements

- even if we activate the sql logs in postgres we will see that postgres still loggin one statement after the other event if the statement is the same:

```
log_statement = 'all
```

There is an optimisation.
```
PGSimpleDataSource dataSource = (PGSimpleDataSource) super.dataSource();
dataSource.setReWriteBatchedInserts(true);
```

This way, only one statement will logged and all values will be logged as one


## Criteria API literal handling

If we execute the test class `CriteriaAPILiteralHandlingTest` and consult the logs we will see that for the:

String values

- The generated sql is:
```sql
    select
        match0_.id as id1_2_,
        match0_.at as at2_2_,
        match0_.awayTeam as awayteam3_2_,
        match0_.homeTeam as hometeam4_2_,
        match0_.version as version5_2_ 
    from
        matches match0_ 
    where
        match0_.awayTeam=?
```

Numeric values

- The generated sql is:
```sql
    select
        match0_.id as id1_2_,
        match0_.at as at2_2_,
        match0_.awayTeam as awayteam3_2_,
        match0_.homeTeam as hometeam4_2_,
        match0_.version as version5_2_ 
    from
        matches match0_ 
    where
        match0_.version=1
```

The default value is auto, but the behavior can be changed by using the following configuration:
```
<property name="hibernate.criteria.literal_handling_mode" value="auto"/>

<property name="hibernate.criteria.literal_handling_mode" value="bind" />

<property name="hibernate.criteria.literal_handling_mode" value="inline" />
```


## IN query default parameter handling
```xml
<property name="hibernate.query.in_clause_parameter_padding" value="true" />
```

---

# PERSISTENCE CONTEXT AND FLUSHING


## Flush modes

JPA defines two automatic flush mode types:

- `FlushModeType.AUTO` 
  - is the default mode and triggers a flush before every query (JPQL or native SQL query) execution and prior to committing a transaction.
  - execute the Flush before every JPQL or Native query, and it is good, because only this way we can achieve that all changed are already in the database we the query are executed in the database.

- `FlushModeType.COMMIT` only triggers a flush before a transaction commit.


Hibernate defines four flush modes:

- `FlushMode.AUTO` 
  - is the default Hibernate API flushing mechanism, and, while it flushes the Persistence Context on every transaction commit, it does not necessarily trigger a flush before every query execution.

- `FlushMode.ALWAYS` 
  - flushes the Persistence Context prior to every query (HQL or native SQL query) and before a transaction commit.

- `FlushMode.COMMIT` 
  - triggers a Persistence Context flush only when committing the currently running transaction. The same beaver then the JPA `FlushModeType.COMMIT`

- `FlushMode.MANUAL` 
  - disables the automatic flush mode, and thePersistence Context can only be flushed manually.


## Flush operation order

Towards the end of the Persistence Context flush, when all EntityAction(s) are in place, Hibernate executes them in a very strict order:

1. `OrphanRemovalAction` - delete statements of Orphan entities
2. `EntityInsertAction` And `EntityIdentityInsertAction` - the insert statements
3. `EntityUpdateAction` - updates statements
4. `CollectionRemoveAction` - element collection or ToMany delete statements
5. `CollectionUpdateAction` - element collection or ToMany update statements
6. `CollectionRecreateAction` - element collection or ToMany insert statements
7. `EntityDeleteAction` - delete statements, this can cause lots of issues

