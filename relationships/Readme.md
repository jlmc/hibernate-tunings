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

In a bidirectional association, only one side can control the underlying table relationship. For
the bidirectional `@OneToMany` mapping, it is the child-side `@ManyToOne` association in charge of
keeping the foreign key column value in sync with the in-memory `Persistence Context`. This
is the reason why the bidirectional @OneToMany relationship must define the mappedBy attribute,
indicating that it only mirrors the @ManyToOne child-side mapping.

One of the major advantages of using a bidirectional association is that entity state transitions
can be cascaded from the parent entity to its children. In the following example, when
persisting the parent Post entity, all the PostComment child entities are persisted as well.







```java
@Entity
public class Movie {
    @Id private Integer id;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<Review> reviews = new HashSet<>();
        
    public void addReview(Review review) {
        this.reviews.add(review);
        review.setMovie(this);
    }
    
    public void removeReview(Review review) {
        reviews.remove(review);
        review.setMovie(null);
    }
}
  
  
@Entity
public class Review {
    @Id private Integer id;
   
    private String comment;
   
    @ManyToOne
    @JoinColumn(
            name = "movie_id",
            updatable = false,
            nullable = false
    )
    private Movie movie;
    
    void setMovie(final Movie movie) {
        this.movie = movie;
    }
}
```


#### Unidirectional