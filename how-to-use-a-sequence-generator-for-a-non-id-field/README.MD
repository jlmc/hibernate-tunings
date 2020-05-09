# How to map generated values

## Problem

- Suppose your database contains a table that contains some columns that are defined at the time of insertion (or at the time of updating). These mechanisms, depending on the type of database, can be: 
    - trigger 
    - sequence 
    - serial 
    - identifier 
    - default value
- How can we map this column so that the JPA or hibernate retrieves the value after it gets generated? 

---

## Solutions

#### Jpa Specification

- The Jpa specifications do not prevent this type of situations.
- JPA is not able to automatically create a value for the non-id-properties. The `@GeneratedValue` annotation is only used in conjunction with `@Id` to create auto-identifiers.
- The `@GeneratedValue` annotation just define that the database is generating this value itself.

- The solution could be to create a separate entity with a generated "ID", as in the following example:

```java
import javax.persistence.*; 

@Entity
public class EventIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventIdentifierGen")
    @SequenceGenerator(name = "eventIdentifierGen", sequenceName = "event_identifier_seq", allocationSize = 6)
    private Integer id;

    // Omitting the getter methods 
}
```

```java
import javax.persistence.*;
import java.time.Instant;

@Entity @Table(name = "occurrence")
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "occurrence_seq")
    @SequenceGenerator(name = "occurrence_seq", sequenceName = "occurrence_seq", allocationSize = 3)
    private Integer id;

    private String description;

    @OneToOne(optional = true,
            //orphanRemoval = true, // if you want to remove also the records of the EventIdentifier when you delete one Occurrence, The CascadeType.REMOVE will have the same effect
            cascade = {MERGE, PERSIST, DETACH})
    @JoinColumn(name = "event_id")
    private EventIdentifier eventIdentifier;

    @Column(name = "stamp", insertable = false, updatable = false, columnDefinition = "timestamp not null default now()")
    private Instant stamp;

    //@formatter:off
    protected Occurrence() {}
    //@formatter:on

    private Occurrence(final String description, final EventIdentifier eventIdentifier) {
        this.description = description;
        this.eventIdentifier = eventIdentifier;
    }

    public static Occurrence of(final String description) {
        return new Occurrence(description, new EventIdentifier());
    }
    
    // Omitting the getter methods 

}
```
- For the example entities, if we have the next case method:

```java
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class DatabaseGeneratedValueWithJPATest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseGeneratedValueWithJPATest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_persist_entity_with_generated_value() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        Occurrence occurrence = Occurrence.of("Roger Federer wins 2017 Wimbledon Championships");

        em.persist(occurrence);

        LOGGER.info(" -- Before the flush [{}]", occurrence);

        em.flush();

        LOGGER.info(" -- Before the commit [{}]", occurrence);

        em.getTransaction().commit();
        LOGGER.info(" -- After the commit [{}]", occurrence);

        em.refresh(occurrence);
        LOGGER.info(" -- After the refresh [{}]", occurrence);

        em.getTransaction().begin();
        LOGGER.info(" -- Removing the Occurrence [{}]", occurrence);
        em.remove(occurrence);
        em.flush();
        em.getTransaction().commit();
    }
}
```
- The execution will generate the following output:
```
DEBUG [org.hibernate.engine.transaction.internal.TransactionImpl] - begin
DEBUG [org.hibernate.SQL] -     call next value for occurrence_seq
DEBUG [org.hibernate.SQL] -     call next value for occurrence_seq
DEBUG [org.hibernate.SQL] -     call next value for event_identifier_seq
DEBUG [org.hibernate.SQL] -     call next value for event_identifier_seq

INFO  [io.costax.models.specification.DatabaseGeneratedValueWithJPATest] -  -- Before the flush [Occurrence{id=1, description='Roger Federer wins 2017 Wimbledon Championships', eventIdentifier=EventIdentifier{id=1}, stamp=null}]

DEBUG [org.hibernate.SQL] - insert into  event_identifier (id) values (?)
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [INTEGER] - [1]

18:09:40,477 DEBUG [org.hibernate.SQL] - insert into occurrence (description, event_id, id) values (?, ?, ?)
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [VARCHAR] - [Roger Federer wins 2017 Wimbledon Championships]
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [2] as [INTEGER] - [1]
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [3] as [INTEGER] - [1]

INFO  [io.costax.models.specification.DatabaseGeneratedValueWithJPATest] -  -- Before the commit [Occurrence{id=1, description='Roger Federer wins 2017 Wimbledon Championships', eventIdentifier=EventIdentifier{id=1}, stamp=null}]

DEBUG [org.hibernate.engine.transaction.internal.TransactionImpl] - committing

INFO  [io.costax.models.specification.DatabaseGeneratedValueWithJPATest] -  -- After the commit [Occurrence{id=1, description='Roger Federer wins 2017 Wimbledon Championships', eventIdentifier=EventIdentifier{id=1}, stamp=null}]

INFO  [io.costax.models.specification.DatabaseGeneratedValueWithJPATest] - -- Execution refresh method

DEBUG [org.hibernate.SQL] - 
    select
        occurrence0_.id as id1_2_0_,
        occurrence0_.description as descript2_2_0_,
        occurrence0_.event_id as event_id4_2_0_,
        occurrence0_.stamp as stamp3_2_0_ 
    from
        occurrence occurrence0_ 
    where
        occurrence0_.id=?
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [INTEGER] - [1]

18:09:40,501 INFO  [io.costax.models.specification.DatabaseGeneratedValueWithJPATest] -  -- After the refresh [Occurrence{id=1, description='Roger Federer wins 2017 Wimbledon Championships', eventIdentifier=EventIdentifier{id=1}, stamp=2020-05-09T18:09:40.479102Z}]

```

- Conclusions:
    - JPA do not allow Entity that is not associated with a database table. So it will be necessary a extra table to have the identifier records.
       - Advantage:
            - Having a business entity to the Identifier is a good practices of Domain driven design.
            - Contextually, the database has become more normalized.
       - Disadvantage
            - One more table is necessary.
            - More SQL statements will be necessary to insert and fetch entities.
    - After the sql insert statement, hibernate will execute an extra select statement to resolve the values generated by the database.
    - The select statement occurred before the commit instruction, it's occurs right the way after the insert statement. So it is not necessary to execute any `EntityManager.flush()` or even `EntityManager.refresh(Object)` method to resolve the values.
    - Because we are using sequences the values of the `EventIdentifier.id` can be resolved before any insert statement execution, but this is because we are using hibernate was JPA implementation and with hibernate version 5.x and higher it uses the [pooled optimizer algorithm](../Identifiers/4.2-hibernate-sequence-optimizers.md) by default when the sequence allocation size is greater than 1. So, it is a hibernate feature.
    - The database generated properties are only resolved after the `EntityManager.refresh(Object)` executions, witch execute one select for all fields of the entity `Occurrence`.
        - It is only one SQL Select Statements because we are not using `CascadeType.ALL` or `CascadeType.REFRESH` in the mapping of `EventIdentifier` properties, If `CascadeType.ALL` or `CascadeType.REFRESH` was used then there would be 2 Selects Statements.


#### Hibernate

- We can annotate the entity attribute with `@Generated(GenerationTime value)`.
- This annotation tell Hibernate that the database will generate the value of the attribute.
- The `GenerationTime` enum tells Hibernate when the database will generate the value. It can either do this `NEVER`, only on `INSERT` or `ALWAYS` (on insert and update). Hibernate then executes an additional query to retrieve the generated value from the database.

- Consider the following example table:
```sql
create sequence rank_id_seq start with 1 increment by 1;
create table Athlete (
    id integer generated by default as identity,
    born_at date not null,
    name varchar(255),
    rank Integer not null default NEXTVAL('rank_id_seq'),
    stamp timestamp not null default now(),
    primary key (id)
);
```
- For the previous example table we have the flowing entity:
```java
import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
public class Athlete {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
  
    @Column(name = "born_at", nullable = false, updatable = false)
    private LocalDate bornAt;
  
    @Generated(GenerationTime.INSERT)
    @Column(name = "stamp", insertable = false, updatable = false, columnDefinition = "timestamp not null default now()")
    private Instant stamp;

    @Generated(GenerationTime.INSERT)
    @Column(name = "rank", insertable = false, updatable = false, columnDefinition = "Integer not null default NEXTVAL('rank_id_seq')")
    private Long rank;

    //@formatter:off
    protected Athlete() {}
    //@formatter:on

    private Athlete(final String name, final LocalDate bornAt) {
        this.name = name;
        this.bornAt = bornAt;
    }

    public static Athlete of(final String name, final LocalDate bornAt) {
        return new Athlete(name, bornAt);
    }

    // Omitting getter ...
}
```
- We can see that the `Athlete` entity declares two properties that are generated by the database at insert time, the `stamp` and `rank`.
```java
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;

public class DatabaseGeneratedValueWithHibernateTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseGeneratedValueWithHibernateTest.class);
    
    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_persist_entity_with_generated_value() {
        EntityManager em = provider.em();
        em.getTransaction().begin();
    
        Athlete rogerFederer = Athlete.of("Roger Federer", LocalDate.parse("1981-08-08"));
    
        em.persist(rogerFederer);
    
        LOGGER.info("--Before the commit the transaction [{}]", rogerFederer);
    
        em.getTransaction().commit();
    
        LOGGER.info("-- Before the commit the transaction [{}]", rogerFederer);
    
        System.out.println(">> The output  " + rogerFederer);
    }
}
```
- The execution will generate the following output:
```
DEBUG [org.hibernate.engine.transaction.internal.TransactionImpl] - begin

DEBUG [org.hibernate.SQL] - insert  into Athlete  (id, born_at, name)  values  (null, ?, ?)
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [DATE] - [1981-08-08]
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [2] as [VARCHAR] - [Roger Federer]

DEBUG [org.hibernate.SQL] -  select athlete_.rank as rank4_0_, athlete_.stamp as stamp5_0_ from Athlete athlete_  where athlete_.id=?
TRACE [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [1] as [INTEGER] - [1]

INFO  [io.costax.models.hibernate.DatabaseGeneratedValueWithHibernateTest] - --Before the commit the transaction [Athlete{id=1, name='Roger Federer', bornAt=1981-08-08, stamp=2020-05-09T16:53:34.696828Z, rank=1}]
DEBUG [org.hibernate.engine.transaction.internal.TransactionImpl] - committing
INFO  [io.costax.models.hibernate.DatabaseGeneratedValueWithHibernateTest] - -- After the commit the transaction [Athlete{id=1, name='Roger Federer', bornAt=1981-08-08, stamp=2020-05-09T16:53:34.696828Z, rank=1}]

>> The output  Athlete{id=1, name='Roger Federer', bornAt=1981-08-08, stamp=2020-05-09T16:53:34.696828Z, rank=1}
```

- Conclusions:
    - After the sql insert statement, hibernate will execute an extra select statement to resolve the values generated by the database.
    - The select statement occurred before the commit instruction, it's occurs right the way after the insert statement. So it is not necessary to execute any `EntityManager.flush()` or even `EntityManager.refresh(Object)` method to resolve the values.


#### Alternative Solutions

1. We can implement your own Generated annotation to mark each property that is generated by the database, 
    - implements a custom `org.hibernate.event.spi.PostInsertEventListener` or `org.hibernate.event.spi.PreInsertEventListener`
    - take a look in the example project [intercept-entity-changes-with-hibernate-event-listeners](intercept-entity-changes-with-hibernate-event-listeners) to understand how can implement and register those classes.
    
2. Having a method in the repository with the responsibility of fetching and returning this value, will be the worst possible solution and should only be used in a limit situation. Some disadvantages of this approach will be:
    - The code will be confused, messed and out of context with an infrastructure detail.
    - The application will have several points of failure / change. We could have more duplicated code.
    - The developer needs to remember that whenever it is necessary to create an X record, certain logic also must be executed!



