package io.costax.hibernatetunnig.graphs.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;


@NamedEntityGraph(
        name = "graph.AuthorBooksPublisherEmployee",
        attributeNodes = @NamedAttributeNode(value = "books", subgraph = "subgraph.book"),
        subgraphs = {
                @NamedSubgraph(name = "subgraph.book",
                        attributeNodes = @NamedAttributeNode(value = "publisher", subgraph = "subgraph.publisher")),
                @NamedSubgraph(name = "subgraph.publisher",
                        attributeNodes = @NamedAttributeNode(value = "employees")) })

@Entity
public class Author {

    @Id
    private Long id;

    @Version
    private int version;

    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "book_author",
            joinColumns = @JoinColumn(name = "author_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "books_id", nullable = false, updatable = false)
    )
    private Set<Book> books = new HashSet<>();

    public Author() {
    }

    public Author(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public Set<Book> getBooks() {
        return Set.copyOf(books);
    }

    public void addBook(Book b) {
        books.add(b);
        b.addAuthor(this);
    }

    public void removeBook(Book b) {
        books.remove(b);
        b.removeAuthor(this);
    }
}
