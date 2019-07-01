# Relationships

- Many to One

- One to Many

- Many to Many

- Element Collection

- One To One


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


### Bidirectional

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

### Unidirectional 


The unidirectional `@OneToMany` association is very tempting because the mapping is simpler than its bidirectional counterpart. 
Because there is only one side to take into consideration, there is no need for helper methods and the mapping does not feature a mappedBy attribute either

####  Unidirectional with 3 tables

It is less efficient than the unidirectional `@ManyToOne` mapping or the bidirectional `@OneToMany` association. 
This **Unidirectional @OneToMany association does not map to a `one-to-many` table relationship**. 
Because there is no `@ManyToOne` side to control this relationship, Hibernate uses a separate junction table to manage the association between a parent row and its child records.

```java
@Entity
public class TvChannel {

    @Id
    private String id;
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TvProgram> programs = new HashSet<>();

    public void addProgram(TvProgram tvProgram) {
        this.programs.add(tvProgram);
    }

    public void removeProgram(TvProgram tvProgram) {
        this.programs.remove(tvProgram);
    }
}

@Entity
public class TvProgram {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, 
            generator = "tv_program_seq") // this will with JPA 2.2 will generate a sequence named 'tv_program_seq' with with default value defined in the @SequenceGenerator annotation
    private Integer id;
    
    private LocalTime start;

    private LocalTime end;

    private String content;
}
```

The previous Entities configuration will generate 3 tables:

![OneToMany-3-Table-db-diagram](docs/OneToMany-3-Table-db-diagram.png)



#### Unidirectional with @JoinColumn

The next Entities configuration will generate 2 tables, it is the best option in terms of complexity.


```java
@Entity 
public class Movie {
    
        @OneToMany(
                fetch = FetchType.LAZY, 
                cascade = CascadeType.ALL, 
                orphanRemoval = true)
        @JoinColumn(name = "movie_id", 
                    updatable = false, 
                    nullable = false)
        //@OrderColumn()
        @OrderBy("li asc ")
        List<Scene> scenes = new ArrayList<>();
}
  
@Entity
public class Scene {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE, 
            generator = "scene_id_generator")
    @SequenceGenerator(
            name="scene_id_generator", 
            sequenceName = "scene_seq", 
            initialValue = 1,
            allocationSize = 10)
    private Integer id;

    private int li = 0;
}
```

![OneToMany-2-Table-db-diagram](docs/OneToMany-2-Table-db-diagram.png)


IMPORTANT NOTE:

**Bidirectional `@OneToMany` with `@JoinColumn` relationship**

The @OneToMany with @JoinColumn association can also be turned into a bidirectional relationship, but it requires instructing the child-side to avoid any insert and update synchronization:

```java

class Scene {
    ...   

    @ManyToOne
    @JoinColumn(name = "movie_id", insertable = false, updatable = false)
    private Movie movie;
}
```

The redundant update statements are generated for both the unidirectional and the bidirectional association, so the most efficient foreign key mapping is the @ManyToOne association.



## Element Collection

```java
@Entity
public class Actor {

    @Id
    private Integer id;

    @ElementCollection(
            fetch = FetchType.LAZY)
    @JoinTable(
            name = "actor_language",
            joinColumns = { @JoinColumn(name = "actor_id", nullable = false) })
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Set<Language> languages = new HashSet<>();
    
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
                name = "actor_prize",
                joinColumns = @JoinColumn(name = "actor_id", nullable = false, updatable = false))
    @AttributeOverrides({
                @AttributeOverride(name = "at", column = @Column(name = "recived_at", nullable = false, updatable = false)),
                @AttributeOverride(name = "value", column = @Column(name = "prize_value", nullable = false, updatable = false))
    })
    private Set<Prize> prizes = new HashSet<>();
}

@Embeddable
public class Prize implements Serializable {

    private OffsetDateTime at;
    private BigDecimal value;
}
```

## Many To Many

### Unidimentional

For `@ManyToMany` associations, CascadeType.REMOVE does not make too much sense when both sides represent independent entities. 
In this case, removing a Event entity should not trigger a Developer removal because the Tag can be referenced by other posts as well. 
The same arguments apply to orphan removal since removing an entry from the tags collection should only delete the junction record and not the target Tag entity.

For both unidirectional and bidirectional associations, it is better to avoid the `CascadeType.REMOVE` mapping. 
Instead of CascadeType.ALL, the cascade attributes should be declared explicitly (e.g. CascadeType.PERSIST, CascadeType.MERGE)

```
@Entity
@Table(name = "event")
public class Event {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_developer",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id"))
    Set<Developer> developers = new HashSet<>();
}

```

### Bidirectional

```

class Developer {

    @ManyToMany(mappedBy = "developers")
    private Set<Event> events = new HashSet<>();
}

@Entity
public class Event {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_developer",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id"))
    Set<Developer> developers = new HashSet<>();
    
    public void add(Developer dev) {
        developers.add(dev);
        dev.getEvents().add(this);
    }
    public void remove(Developer tag) {
         developers.remove(dev);
         dev.getEvents().remove(this);
    }
}
```

Like any other bidirectional associations, both sides must in sync, so the helper methods are being added here as well. 
For a @ManyToMany association.

The helper methods must be added to the entity that is more likely to interact with. 
In this example, the business logic manages Event(s) rather than Developer(s), so the helper methods are added to the Event entity


## using java.util.Map We can also use a Map

```java
@Entity
public class TvSerie {

    @Id
    private Integer id;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tv_serie_episode", joinColumns = @JoinColumn(name =     "tv_serie_id"))
    @MapKeyColumn(name = "field_key", length = 50)
    @Column(name = "field_value", length = 100)
   // @BatchSize(size = 20)
    private Map<String, String> episodes = new HashMap<>();
    
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "tv_serie_prize", joinColumns = @JoinColumn(name = "tv_serie_id"))
    //@MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "field_key", length = 50)
    @AttributeOverrides({
            @AttributeOverride(name = "at", column = @Column(name = "recived_at", nullable = false, updatable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "prize_value", nullable = false, updatable = false))
    })
    private Map<String, Prize> prizes = new HashMap<>();
}
```

```
@ManyToMany
@JoinTable(
        name="relation_table_name",
        joinColumns = { @JoinColumn(name = "fk_current_id", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "fk_group", referencedColumnName = "id")})
@MapKey(name = "secound_entity_column")
private Map<String, SecoundEntity> secounds = new HashMap<String, SecoundEntity>();

```


## ManyToMany - ManyToOne Alternative

Consider the DB Diagram:

![ManyToManyAternativeWithManyToOne](docs/ManyToManyAternativeWithManyToOne.png)

Note that we have a extra column personage in the relationship table


```java
@Entity
@Table(name = "movie_actor_personage")
public class MovieActorPersonage {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(
                    name = "movieId", 
                    column = @Column(
                            name = "movie_id", 
                            nullable = false, 
                            updatable = false)),
            @AttributeOverride(
                    name = "actorId", 
                    column = @Column(
                            name = "actorId", 
                            nullable = false, 
                            updatable = false))
    })
    private MovieActorPersonageId id;

    @ManyToOne
    @MapsId("movieId")
    private Movie movie;

    @ManyToOne
    @MapsId("actorId")
    private Actor actor;

    private String personage;

    protected MovieActorPersonage() {}

    public MovieActorPersonage(Movie movie, Actor actor, String personage) {
        this.movie = movie;
        this.actor = actor;
        this.id = MovieActorPersonageId.of(movie.getId(), actor.getId());

        this.personage = personage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieActorPersonage that = (MovieActorPersonage) o;
        return Objects.equals(movie, that.movie) &&
                Objects.equals(actor, that.actor);
    }
    @Override
    public int hashCode() {
        return Objects.hash(movie, actor);
    }
}
 
@Embeddable
public class MovieActorPersonageId implements Serializable {

    private Integer movieId;
    private Integer actorId;

    protected MovieActorPersonageId() {}

    private MovieActorPersonageId(final Integer movieId, final Integer actorId) {
        this.movieId = movieId;
        this.actorId = actorId;
    }

    public static MovieActorPersonageId of(final Integer movieId, final Integer actorId) {
        return new MovieActorPersonageId(movieId, actorId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieActorPersonageId)) return false;
        final MovieActorPersonageId that = (MovieActorPersonageId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(actorId, that.actorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, actorId);
    }
}


@Entity
public class Movie {
    
    @Id
    private Integer id;
    
    // omit other properties
    
    @OneToMany(
               mappedBy = "movie", 
               cascade = CascadeType.ALL, 
               orphanRemoval = true)
    private Set<MovieActorPersonage> actors = new HashSet<>();
    
    public void removePersonage(Actor actor) {
        for (Iterator<MovieActorPersonage> iterator = actors.iterator(); iterator.hasNext(); ) {
            MovieActorPersonage movieActorPersonage = iterator.next();

            if (movieActorPersonage.getMovie().equals(this) && movieActorPersonage.getActor().equals(actor)) {

                iterator.remove();

                movieActorPersonage.getActor().getMovies().remove(movieActorPersonage);
                movieActorPersonage.setMovie(null);
                movieActorPersonage.setActor(null);
                //break;
            }
        }
    }    
    
}

```

The Movie entity maps the bidirectional @OneToMany side of the Movie @ManyToOne association:

```
@OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
private Set<MovieActorPersonage> movieActorPersonages = new HashSet<>();
```

If necessary the Actor entity can also maps the bidirectional @OneToMany side of the actor @ManyToOne association:

```
@OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
private List<PostTag> movies = new ArrayList<>();
```

This way, the bidirectional @ManyToMany relationship is transformed in two bidirectional @OneToMany associations.

The removeTag helper method 'removePersonage' is much more complex because it needs to locate the MovieActorPersonage associated with the current Movie entity and the Actor that is being disassociated.


---

## @OneToOne

From a database perspective, the one-to-one association is based on a foreign key that is constrained to be unique. 
This way, a parent row can be referenced by at most one child record only.
In JPA, the @OneToOne relationship can be either unidirectional or bidirectional

### Bidirectional

This is the most common case found the pre-existing projects.

![OneToOneBidirectionalTraditionanl.png](docs/OneToOneBidirectionalTraditionanl.png)


Note that: the fk festival_id in the Festivaldetails table is a unique constrain.

The Examples of the Best use of this is demonstrated in the test class: io.costax.relationships.OneToOneTradicionalBidimentionalTest

The JPA Class was the following:


```java

@Entity
public class Festival {
    @Id private Integer id;
    private String name;

    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private FestivalDetails details;

    public void setDetails(final FestivalDetails details) {
        if (details == null) {
            if (this.details != null) {
                this.details.setFestival(null);
            }
        } else {
            details.setFestival(this);
        }
        this.details = details;
    }
}

@Entity
public class FestivalDetails {
    @Id private Integer id;

    @OneToOne
    @JoinColumn(name = "festival_id", referencedColumnName = "id", unique = true)
    private Festival festival;

    private String country;
    private String locality;
    private OffsetDateTime happensAt;

    public void setFestival(final Festival festival) {
        this.festival = festival;
    }
}

```

The parent-side defines a mappedBy attribute because the child-side (which can is still in charge of this JPA relationship:

```
@OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
@LazyToOne(LazyToOneOption.NO_PROXY)
private FestivalDetails details;
```

Because this is a bidirectional relationship, the Festival entity must ensure that both sides of this relationship are set upon associating a FestivalDetails entity:

```
    public void setDetails(final FestivalDetails details) {
        if (details == null) {
            if (this.details != null) {
                this.details.setFestival(null);
            }
        } else {
            details.setFestival(this);
        }
        this.details = details;
    }
```

Unlike the parent-side `@OneToMany`relationship where Hibernate can simply assign a proxy even if the child collection is empty, 
The `@OneToOne` relationship must decide if to assign the child reference to null or to an Object, be it the actual entity object type or a runtime Proxy.

This is an issue that affects the parent-side @OneToOne association, while the child-side, which has an associated foreign key column, knows whether the parent reference should be null or not. For this reason, the parent-side must execute a secondary query to know if there is a mirroring foreign key reference on the child-side.

`em.find(Festival.class, 5);`

```
select * from Festival festival0_ where festival0_.id=?

select
        festivalde0_.id as id1_5_1_,
        festivalde0_.country as country2_5_1_,
        festivalde0_.festival_id as festival5_5_1_,
        festivalde0_.happensAt as happensA3_5_1_,
        festivalde0_.locality as locality4_5_1_,
        festival1_.id as id1_4_0_,
        festival1_.name as name2_4_0_ 
    from
        FestivalDetails festivalde0_ 
    left outer join
        Festival festival1_ 
            on festivalde0_.festival_id=festival1_.id 
    where
        festivalde0_.festival_id=?
```

If the application developer only needs parent entities, the additional child-side secondary queries will be executed unnecessarily, and this might affect application performance. 
The more parent entities are needed to be retrieved, the more obvious the secondary queries performance impact gets.

**Limitations**

Even if the foreign key is NOT NULL and the parent-side is aware about its non-nullability through the optional attribute 
(e.g. @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, optional = false)),
 
Hibernate still generates a secondary select statement.

For every managed entity, the Persistence Context requires both the entity type and the identifier, so the child identifier must be known when loading the parent entity, and the only way to find the associated post_details primary key is to execute a secondary query. Because the child identifier is known when using @MapsId, in future, [HHH-10771](https://hibernate.atlassian.net/browse/HHH-10771) a should address the secondary query issue.

Bytecode enhancement is the only viable workaround. However, it only works if the parent side is annotated with `@LazyToOne(LazyToOneOption.NO_PROXY)` and the child side is not using `@MapsId`. 
Because it’s simpler and more predictable, the unidirectional @OneToOne relationship is often preferred.


