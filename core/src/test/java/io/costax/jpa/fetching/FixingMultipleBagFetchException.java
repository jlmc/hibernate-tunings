package io.costax.jpa.fetching;

import io.costax.jpa.util.HSQLDBJPATest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Fetching all associations in a LAZY can bring us a new new issue:
 */
public class FixingMultipleBagFetchException extends HSQLDBJPATest {

    @Override
    protected void additionalProperties(final Properties properties) {
        properties.put("hibernate.format_sql", "true");
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                Book.class, Author.class, Review.class
        };
    }

    @BeforeEach
    void setUp() {
        doInJPA(em -> {
            for (int i = 1; i <= 20; i++) {
                createBook(em, i);
            }
        });
    }

    @Test
    @Disabled("To provoke the problem, please replace the Book#Sets collection to Lists")
    void executeQueryThatMayThrowMultipleBagFetchException() {
        //
        final EntityManager entityManager = entityManager();

        List<Book> books =
                entityManager
                        .createQuery(
                                """
                                        select distinct b
                                        from Book b 
                                          left join fetch b.authors
                                          left join fetch b.reviews
                                        """, Book.class)
                        .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                        .getResultList();

        fail("To provoke the problem, please replace the Book#Sets collection to Lists");
    }


    /**
     * Option 1: Use a Set instead of a List
     */
    @Test
    void useSetsInsteadLists() {
        //
        final EntityManager entityManager = entityManager();

        List<Book> books =
                entityManager
                        .createQuery(
                                """
                                        select distinct b
                                        from Book b 
                                          left join fetch b.authors
                                          left join fetch b.reviews
                                        """, Book.class)
                        .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                        .getResultList();

        entityManager.close();
        assertEquals(20, books.size());
    }

    /**
     * Option 2: Split it into multiple queries
     */
    @Test
    void fetchSplitingIntoMultipleQueries() {
        final EntityManager entityManager = entityManager();

        @SuppressWarnings("UnusedAssignment")
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

    private Book createBook(final EntityManager em, final Integer integer) {

        final Book book = Book.createBook("Book example " + integer);
        em.persist(book);

        final List<Author> authors =
                Stream.iterate(1, i -> i < 5, i -> i + 1)
                        .map(i -> Author.createAuthor(String.format("Author [%d] [%d]", integer, i)))
                        .collect(Collectors.toList());
        authors.forEach(em::persist);
        book.addAuthors(authors);


        final List<Review> reviews =
                Stream.iterate(1, i -> i < 5, i -> i + 1)
                        .map(i -> Review.createReview(String.format("Review [%d] [%d]", integer, i)))
                        .collect(Collectors.toList());
        authors.forEach(em::persist);
        book.addReviews(reviews);

        return book;
    }


    // ********************
    // Mapping

    @Getter
    @NoArgsConstructor
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
        private final Set<Author> authors = new HashSet<>();
        //private final List<Author> authors = new ArrayList<>();

        @OneToMany(
                orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
                cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE
                })
        @JoinColumn(name = "bookId", nullable = false, updatable = false)
        private final Set<Review> reviews = new HashSet<>();
        //private List<Review> reviews = new ArrayList<>();

        private Book(final String title) {
            this.title = title;
        }

        public static Book createBook(final String title) {
            return new Book(title);
        }

        public void addAuthors(final List<Author> authors) {
            this.authors.addAll(authors);
        }

        public void addReviews(final List<Review> reviews) {
            this.reviews.addAll(reviews);
        }
    }


    @NoArgsConstructor
    @Entity(name = "Author")
    static class Author {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;

        private Author(final String name) {
            this.name = name;
        }

        public static Author createAuthor(final String name) {
            return new Author(name);
        }
    }

    @NoArgsConstructor
    @Entity(name = "Review")
    static class Review {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String content;

        private Review(final String content) {
            this.content = content;
        }

        public static Review createReview(final String content) {
            return new Review(content);
        }
    }
}
