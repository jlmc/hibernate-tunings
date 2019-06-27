# Relationships

- Many to One

- One to Many

- Many to Many

- Element Collection


##  Many to One 

```java
@Entity
public class Movie {
    @Id
    private Integer id;
    @Column(name = "title", nullable = false)
    private String title;
    
    // By default the fetch value is FetchType.EAGER, 
    // changing the behavior of fetch to LAZY is a great improvement, 
    // even by it is always possible to change this behavior at query execution time (using a graph or a fetch). 
    // The opposite is not possible
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "director_id", referencedColumnName = "id", nullable = false)
    private Director director;
}
```


## One to Many 


#### Bidirectional


```java
@Entity
public class Article {
    @Id private Integer id;
    
    @ManyToOne
    @JoinColumn(
            name = "author_id", 
            referencedColumnName = "id", 
            nullable = false)
    private Author author;
}
```

#### Unidirectional
