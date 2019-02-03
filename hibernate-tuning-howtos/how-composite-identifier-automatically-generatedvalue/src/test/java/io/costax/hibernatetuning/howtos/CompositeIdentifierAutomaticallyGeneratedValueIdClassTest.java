package io.costax.hibernatetuning.howtos;

import io.costax.hibernatetuning.howtos.book.Book;
import io.costax.hibernatetuning.howtos.book.PK;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * The Current Book IdClass(PK.class) mapping only works if all the generated values are sequences.
 * Otherwise we should use a @EmbeddedId mapping strategy.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompositeIdentifierAutomaticallyGeneratedValueIdClassTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @After
    @Before
    public void before() {
        provider.beginTransaction();
        final Session session = provider.em().unwrap(Session.class);

        session.doWork(connection -> {

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("delete from public.book");
            } catch (Exception ignore) {
            }
        });


        provider.commitTransaction();
    }

    @Test
    public void t0_should_persist_entity_with_composed_generated_key() {

        provider.beginTransaction();
        final EntityManager em = provider.em();

        Book book = Book.of(1, "Building Microservices");
        em.persist(book);

        Book book2 = Book.of(1, "Building Microservices version 2");
        em.persist(book2);

        em.flush();
        provider.commitTransaction();

        PK key = new PK(book2.getRegistrationNumber(), 1);
        Book book2Fetched = em.find(Book.class, key);

        assertEquals(
                "Building Microservices version 2",
                book2Fetched.getTitle()
        );
    }
}