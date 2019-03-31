package batching;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "book", schema = "multimedia")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookGenerator")
    @SequenceGenerator(
            name = "bookGenerator",
            schema = "multimedia",
            sequenceName = "book_5_seq",
            allocationSize = 5,
            initialValue = 100)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToMany
    @JoinTable(
            name = "book_author",
            schema = "multimedia",
            joinColumns = {@JoinColumn(name = "book_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "author_id", referencedColumnName = "id")})
    private Set<Author> authors = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private Set<Review> review = new HashSet<>();

    public Book() {
    }

    private Book(final String title, final String description) {
        this.title = title;
        this.description = description;
    }

    public static Book of(final String title, final String description) {
        return new Book(title, description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        final Book serie = (Book) o;
        return this.id != null && Objects.equals(id, serie.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void addActor(final Author actor) {
        this.authors.add(actor);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
