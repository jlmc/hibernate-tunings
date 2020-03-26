# javax.persistence.OrderColumn - JPA annotation

- Specifies a column that is used to maintain the persistent order of a list. 
- The persistence provider is responsible for maintaining the order upon retrieval and in the database. 
- The persistence provider is responsible for updating the ordering upon flushing to the database to reflect any insertion, deletion, or reordering affecting the list.

- The `OrderColumn` annotation is specified on a `OneToMany` or `ManyToMany` relationship or on an element collection of type `List`.
- A Set collection have a much better performance but as consequence the `@OrderColumn(name = "li")` don't work
- The `OrderColumn` annotation is specified on the side of the relationship that references the collection that is to be ordered.
- The order column is not visible as part of the state of the entity or embeddable class.

- In contrast, the `OrderBy` annotation should be used for ordering that is visible as persistent state and maintained by the application. 
- The `OrderBy` annotation is not used when `OrderColumn` is specified.

- The order column must be of `integral type`. 
- The persistence provider maintains a contiguous (non-sparse) ordering of the values of the order column when updating the association or element collection. 
- The order column value for the first element is `0`.


```java
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TvChannel {

    @Id
    private String id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "entry")
    private Set<TvProgram> programs = new HashSet<>();
}

```


```java
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.Objects;


@Entity
public class TvProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tv_program_seq")
    //@SequenceGenerator(name="scene_id_generator", sequenceName = "tv_program_seq", allocationSize = 5, initialValue = 1)
    private Integer id;

    private LocalTime start;

    private LocalTime end;

    private String content;
}
```