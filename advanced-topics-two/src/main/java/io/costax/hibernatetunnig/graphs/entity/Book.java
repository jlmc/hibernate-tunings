package io.costax.hibernatetunnig.graphs.entity;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Book {

    @Id
    private Long id;
    @Version
    private int version;
    private ZonedDateTime publishingDate;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;

    @ManyToMany(mappedBy = "books")
    private Set<Author> authors = new HashSet<>();

    protected void addAuthor(final Author a) {
        authors.add(a);
    }

    protected void removeAuthor(final Author a) {
        authors.remove(a);
    }

    public Set<Author> getAuthors() {
        return Set.copyOf(authors);
    }
}
