package io.costax.hibernatetunnig.graphs;

import io.costax.hibernatetunnig.graphs.entity.Author;
import io.costax.hibernatetunnig.graphs.entity.Book;
import io.costax.hibernatetunnig.graphs.entity.Publisher;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityGraph;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Example of how to use SubGraphs inside SubGraphs.
 * <p>
 * An EntityGraph provides an excellent way to avoid N+1 select issues
 * bu initializing the required lazily fetched associations.
 * <p>
 * The Definitions of the graph is independent of the Query and defines which associations
 * JPA should initialize before returning our query result
 */
@JpaTest(persistenceUnit = "it")
public class CreateAnEntityGraphWithMultipleSubGraphsTest {

    public static final long SARAMAGO_ID = 100L;

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void setUp() {
        provider.doInTx(em -> {
            Author saramago = new Author(100L, "José de Sousa Saramago");
            em.persist(saramago);

            final Publisher postmodernism = new Publisher(1L, "Encyclopedia of Postmodernism");
            em.persist(postmodernism);

            final Book landOfSin = new Book(1L,
                    ZonedDateTime.of(
                            LocalDate.of(1947, Month.FEBRUARY, 5)
                                    .atTime(10, 30), ZoneId.of("Europe/Lisbon")),
                    postmodernism);


            final Book manualOfPaintingAndCalligraphy = new Book(2L,
                    ZonedDateTime.of(
                            LocalDate.of(1977, Month.FEBRUARY, 5)
                                    .atTime(10, 30), ZoneId.of("Europe/Lisbon")),
                    postmodernism);

            saramago.addBook(landOfSin);
            saramago.addBook(manualOfPaintingAndCalligraphy);

            em.flush();
        });
    }

    @AfterEach
    public void tearDown() {
        provider.doInTx(em -> {
            em.createNativeQuery("delete from BOOK_AUTHOR where true").executeUpdate();
            em.createNativeQuery("delete from BOOK where true").executeUpdate();
            em.createNativeQuery("delete from PUBLISHER where true").executeUpdate();
            em.createNativeQuery("delete from AUTHOR where true").executeUpdate();
        });
    }

    @Test
    public void should_create_entity_graph_and_fetch_entity_using_entity_graph() {
        final Author saramago =
                provider.doItWithReturn(em -> {
                    EntityGraph<?> graph = em.createEntityGraph("graph.AuthorBooksPublisherEmployee");
                    TypedQuery<Author> q = em.createQuery("SELECT a FROM Author a WHERE a.id = :authorId", Author.class);
                    q.setHint("javax.persistence.fetchgraph", graph);

                    return q.setParameter("authorId", SARAMAGO_ID).getSingleResult();
                });

        assertNotNull(saramago);
        final Set<Book> books = saramago.getBooks();
        assertEquals(2, books.size());
        final Book[] booksArray = books.toArray(new Book[0]);
        assertNotNull(booksArray[0].getPublisher());
        assertNotNull(booksArray[1].getPublisher());
    }
}
