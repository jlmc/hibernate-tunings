# Mapping BLOBs & CLOBs with JPA and Hibernate

Databases use the data types BLOB (binary large object) and CLOB (character large object) to store large objects, like images and very long texts. JPA and Hibernate provide two kinds of mappings for these types.


we can choice if we want to:

1. Materialize the LOB and map it to a byte[] or a String. 

    - This mapping is defined by the JPA specification 
    - Prioritizes ease of use over performance.


2. Use JDBC’s LOB locators java.sql.Blob and java.sql.Clob. 

   - The LOB locators enable your JDBC driver to optimize for performance, e.g., by **streaming the data**. 
   - This mapping is Hibernate-specific.

---

The mapping of both approaches looks almost identical. 

we just need to annotate your attribute with a `@Lob`annotation.
The deference between the both mapping is the type of your attribute. 

1. In the database we can create a column as OID and TEXT.

In the current examples we will use one table with the following characteristics:

```
create table article_clob
(
    id      bigint primary key,
    content text,
    cover   oid,
    title   character varying(255)
);
```

--- 

### Mapping a LOB to String or byte[]

- The materialized mapping to a String or a byte[] is the most intuitive mapping for most Java developers. 

- Entity attributes of these types are easy to use, and it feels natural to use them in your domain model.

- But Hibernate also needs to fetch all data stored in the LOB immediately and map it to a Java object. Depending on the size of your LOB, this can cause severe performance problems.

- The **JPA specification defines this mapping**. We can not only use it with **Hibernate** but also with **EclipseLink** and **OpenJPA**.

- Creating materialized mappings is very simple. We just need an attribute of type `String` or `byte[]` and annotate it with JPA’s `@Lob` annotation.

```java
public class ArticleBlob {

    @Id
    private Long id;
    private String title;

    @Lob
    private String content;
    @Lob
    private byte[] cover;
}
```

Hibernate can also map nationalized character data types, like `NCHAR`, `NVARCHAR`, `LONGNVARCHAR`, and `NCLOB`. 
To define such a mapping, we need to annotate our entity attribute of type String with Hibernate’s `@Nationalized` annotation instead of `@Lob`.

```java
    @org.hibernate.annotations.Nationalized
    private String content;
```

---

### Mapping a LOB to java.sql.Clob or java.sql.Blob

With Hibernate, we can use the same approach to map our `LOB` to a `java.sql.Clob` or a `java.sql.Blob`. 

- These Java types are not as easy to use as a String or byte[]. **But they enable your JDBC driver to use LOB-specific optimizations, which might improve the performance of your application**. 

- If and what kind of optimizations are used, depends on the JDBC driver and our database.

The mapping is Hibernate-specific and not defined by the JPA specification.

Defining the mapping

1. The cover attribute is now of type Blob.
2. The content attribute is of type Clob.

```java
@Entity
@Table(name = "article_clob")
public class ArticleClob {

    @Id
    private Long id;
    private String title;

    @Lob
    private java.sql.Clob content;
    @Lob
    private java.sql.Blob cover;
}
```


Hibernate also enables us to map the `nationalized` character data types `NCHAR`, `NVARCHAR`, `LONGNVARCHAR`, and `NCLOB` to a `java.sql.Clob`. 
We just need to use the `@org.hibernate.annotations.Nationalized` annotation in the field.

To create a `Blob` object, you can call the `generateProxy` method of the `BlobProxy` with a `byte[]` or an `InputStream`. 
We can call the `generateProxy` method of the `ClobProxy` with a String or a Reader. That makes both proxies very comfortable to use.

Reading a `Blob` or a `Clob` is also not too complicated but requires a little more work than using a `byte[]` or a `String`. 
The `java.sql.Blob` interface provides you with multiple methods to get an `InputStream` or a `byte[]` of the BLOB value. 
The `java.sql.Clob` interface defines various ways to get a `Reader` or a String of the `CLOB` value.

```java
articleClob.setCover(BlobProxy.generateProxy(sourceCover));
articleClob.setContent(ClobProxy.generateProxy(sourceContent));

// ...

InputStream binaryStream = articleClob.getCover().getBinaryStream();
Reader characterStream = articleClob.getContent().getCharacterStream();
```
---

## Lazy loading for LOBs

Solution 1: We can use Basic annotation with the `hibernate-enhance-maven-plugin` plugin with the configuration `enableLazyInitialization = true`

```java
@jakarta.persistence.Basic(fetch = FetchType.LAZY)
```

Solution 2: we can also create a separated entity to put the the LOB and create a relacionship @OneToOne between the two entities.

```java
@Entity
public class ArticleBlobLobs {
 
    @Id
    private Long id;
     
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private ArticleBlob articleBlob;
     
    @Lob
    private String content;
     
    @Lob
    private byte[] cover;
 
    // ...
}
```

```java
public class ArticleBlob {

    @Id
    private Long id;
    private String title;

    @OneToOne(
            mappedBy = "articleBlob",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    //@LazyToOne(LazyToOneOption.NO_PROXY)
    private ArticleBlobLobs lobs;
}
```
