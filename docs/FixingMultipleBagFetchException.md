# How to fix FixingMultipleBagFetchException issues


Fetching all associations in a LAZY can bring us a new issue:

Now we have to use a `JOIN FETCH` query clause, or an `EntityGraph` to fetch the association if we need it.
Otherwise, we will experience the n+1 select issue (which causes several performance issues), or a `org.hibernate.LazyInitializationException`.

If you do that for multiple associations,
Hibernate might throw a `org.hibernate.loader.MultipleBagFetchException`.

In this current test Class example, I'm analysing the two options we have to fix the problem.


## Cause of the MultipleBagFetchException


For a `ToMany` association, Hibernate’s internal naming of the collection types is pretty confusing. Hibernate calls it a `Bag`, if the elements in your `java.util.List` are **unordered**. If they are **ordered**, it’s called a `List`.  
So, depending on your mapping, a `java.util.List` can be treated as a `Bag` or a `List`.  

- Defining the **order** of an association requires an additional annotation and is almost always an overhead. _That’s why you should avoid it and why at least 90% of the association mappings that use a `java.util.List`_ and you should always prefer to use **unordered** mapping, this way Hibernate treats them as a `Bag`.

Here is a simple domain model in which Hibernate treats the Reviews and the Authors of a Book as Bags.

```java
@Entity(name = "Book")
static class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "BookAuthor",
            joinColumns = {@JoinColumn(name = "bookId", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "authorId", nullable = false, updatable = false)}
    )
    private final List<Author> authors = new ArrayList<>();

    @OneToMany(
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinColumn(name = "bookId", nullable = false, updatable = false)
    private List<Review> reviews = new ArrayList<>();

    // ....
}
```

Now if the database have some records, and we execute the following query the will have one exception

```java
entityManager
       .createQuery(
                    """
                    select b
                    from Book b 
                      left join fetch b.authors
                      left join fetch b.reviews
                    """, Book.class)
       .getResultList();
```
- the exceptions will be:
```
java.lang.IllegalArgumentException: org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: [io.costax.jpa.fetching.FixingMultipleBagFetchException$Book.authors, io.costax.jpa.fetching.FixingMultipleBagFetchException$Book.reviews]
...
Caused by: org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags: [io.costax.jpa.fetching.FixingMultipleBagFetchException$Book.authors, io.costax.jpa.fetching.FixingMultipleBagFetchException$Book.reviews]
```


## Fixing the MultipleBagFetchException

we need to know that:

1. If all of your associations only contain a small number of elements, the created cartesian product will be relatively small. In these situations, you can change the types of the attributes that map your associations to a java.util.Set. Hibernate can then fetch multiple associations in 1 query.

2. If at least one of your associations contains a lot of elements, your cartesian product will become too big to fetch it efficiently in 1 query. You should then use multiple queries that get different parts of the required result.

To have a good performance in our applications many times we have to choose between different trade-offs, and there is no one-size-fits-all approach.  
The performance of each option depends on the size of the cartesian product and the number of queries you’re executing. For a relatively small cartesian product, getting all information with 1 query provides you with the best performance. If the cartesian product reaches a certain size, you should better split it into multiple queries.


### Option 1: Use a Set instead of a List

- The easiest approach to fix the `MultipleBagFetchException` is to change the type of the attributes that map your `ToMany` associations to a `java.util.Set`. This is just a small change in the mapping almost certainly it does not require any change in the business code.

##### Performance considerations
- If the cartesian product is relatively small, it might be faster to accept the inefficiency of the cartesian product to reduce the number of queries. This might change if your cartesian product becomes bigger because you select a huge number of Books or if your average Book has been written by a few dozen Authors.


### Option 2: Split it into multiple queries

Fetching huge cartesian products in 1 query is inefficient. It requires a lot of resources in your database and puts unnecessary load on your network. Hibernate and your JDBC driver also need to spend more resources to handle the query result.

You can avoid that by performing multiple queries that fetch different parts of the required graph of entities. If your graph of required entities is complex, you might need to use more queries or fetch more associations with each of them.



Hibernate ensures that within each Session, there is only 1 entity object that represents a specific record in the database. You can use that to resolve foreign key references efficiently or to let Hibernate merge the results of multiple queries.

If you take a look at the following log output, you can see that the Lists returned by both queries contain exactly the same object. In both cases, the Book objects have the reference @1f.

When Hibernate processed the result of the 2nd query, it checked for each record if the 1st level cache already contained an object for that Book entity. It then reused that object and added the returned Review to the mapped association.

```
    @Test
    void fetchSplitingIntoMultipleQueries() {
        final EntityManager entityManager = entityManager();
        List<Book> books =
                entityManager
                        .createQuery(
                                """
                                        select distinct b
                                        from Book b 
                                          left join fetch b.authors
                                        """, Book.class)
                        .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                        .getResultList();

        books = entityManager
                .createQuery(
                        """
                                select distinct b
                                from Book b 
                                  left join fetch b.reviews
                                """, Book.class)
                .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                .getResultList();

        LOGGER.info("{}", books.get(0));
        LOGGER.info("Authors: " + books.get(0).getAuthors().size());
        LOGGER.info("Reviews: " + books.get(0).getReviews().size());
    }
```

- The JPA specification ensures that within each EntityManager/PersistenceContext, there is only 1 entity object that represents a specific record in the database. You can use that to resolve foreign key references efficiently or to let Hibernate merge the results of multiple queries.

> "An EntityManager instance is associated with a persistence context. A persistence context is a set of entity instances in which for any persistent entity identity there is a unique entity instance. Within the persistence context, the entity instances and their lifecycle are managed. The EntityManager interface defines the methods that are used to interact with the persistence context. The EntityManager API is used to create and remove persistent entity instances, to find persistent entities by primary key, and to query over persistent entities." - from  [JPA-2.2 Specification](https://github.com/javaee/jpa-spec/blob/master/jsr338-MR/JavaPersistence.pdf) page 63


- When Hibernate processed the result of the 2nd query, it checked for each record if the `1st level cache already contained an object` for that entity. It then reused that object and added the returned Review to the mapped association.

##### Performance considerations

- If we use multiple queries to get the required graph of entities, we can avoid the creation of a huge cartesian product. This reduces the load on all involved systems and makes it easier to ensure a good performance for all queries.

- But that not necessarily means that this approach is faster than option 1. We are performing more queries than before. Each of them requires a database roundtrip and creates some management overhead in the database, e.g., to create an execution plan. Due to that, this option is only faster than option 1, if the size of the cartesian product creates a bigger overhead than the execution of multiple queries.