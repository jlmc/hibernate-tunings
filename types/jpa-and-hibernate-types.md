# Jpa and Hibernate Types


## Agenda

 - Mapping constructs
 - Column type compactness
 - Mapping a Java Enum


## In JPA and Hibernate, there are three types of mapping constructs. 

* Basic Types: 
    - are used to map various database column types: VARCHAR, INTEGER, FLOAT, Enum, Date and so on.

* Embeddable types: 
    - are used to group multiple column types into a single Java component. They are many times also called as Value Objects.

* Entities: 
    - are usually associated with a table while the entity identifier maps to the underlying table Primary Key.


NOTE: 

More compact the type, the better the performance. If we use fewer bytes to represent a certain value, then we can accommodate more rows per page, which is the smallest block of memory:
More rows per page (memory or disk)
Enum are usually a typical case of poor choice of column type in the database, when we choose:

```java
@Enumerated(EnumType.String)
```

instead of:

```java
@Enumerated(EnumType.ORDINAL)
```

Ordinal would occupy less bytes, that is, it would have a better performance.
But we can still do more for the Enum type, we can make an even better choice:
We could choose the smallest type in the database, in the case of postgres 'tinyint', for example

```java
@Enumerated(EnumType.ORDINAL)
@Column(columnDefinition = "tinyint")
private Status status;
```

## Extra Table

We can solve the weak readability by creating an extra table, using its register as FK.
this increases the robustness of our data.

We can even create an auxiliary entity to increase redemption.

```java
@Entity(name = "PersonStatusInfo") 
@Table(name = "person_status_info") 
public class StatusInfo {
    @Id
    @Column(columnDefinition = "tinyint") 
    private Integer id;
    
    private String name; 
    
    private String description;
}
```


Then in our entity we could use it as follows:

```java
@Entity(name = "Person") 
@Table(name = "person") 
public class Person {

    @Id
    private Long id;

    @Enumerated(EnumType.ORDINAL) 
    @Column(columnDefinition = "tinyint") 
    private Status status;
    
    // this property is ignored in the updated and inserts, it is a read only field, 
    // otherwise we have two field controlling the same database column 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status", insertable = false, updatable = false) 
    private StatusInfo statusInfo;
    
    // other fields
}
```


## PostgreSQL Enum

PostgreSql has a very interesting feature, it allows us to create a custom type as an Enum.

PostgreSQL ENUM TYPE takes 4 bytes.

```postgresql
CREATE TYPE person_status_info AS ENUM (
    'PENDING'
    'APPROVED',
    'SPAM'
)

```

Hibernate then makes it possible to create the custom type for Postgres enum.

```java
@Entity(name = "Person") 
@Table(name = "person")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class Person {

    @Id
    private Long id;

   @Enumerated(EnumType.STRING) 
   @Column(columnDefinition = "person_status_info") 
   @Type(type = "pgsql_enum")
   private Status status;
    
    // other fields
}

```


```java
public class PostgreSQLEnumType extends org.hibernate.type.EnumType {
    
    public void nullSafeSet( 
            PreparedStatement st,
            Object value,
            int index, 
            SharedSessionContractImplementor session) throws HibernateException, SQLException {
        
        if (value == null) { 
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.toString(), Types.OTHER);
        } 
    }
}

```