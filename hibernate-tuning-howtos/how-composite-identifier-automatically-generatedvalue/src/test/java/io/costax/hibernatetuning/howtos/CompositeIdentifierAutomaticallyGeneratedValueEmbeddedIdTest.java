package io.costax.hibernatetuning.howtos;

import io.costax.hibernatetuning.howtos.ibook.IBook;
import io.costax.hibernatetuning.howtos.ibook.IBookKey;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * This mapping is useful for databases that do not support sequences
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CompositeIdentifierAutomaticallyGeneratedValueEmbeddedIdTest {

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

        IBook book = new IBook();
        book.setTitle("Composed Generated Id in Entity using EmbeddedId Annotation");
        IBookKey key = new IBookKey();
        key.setPublisherId(1);
        book.setKey(key);


        em.persist(book);
        em.flush();
        em.clear();

        final IBook iBook = new IBook();
        iBook.setTitle("Composed Generated Id in Entity using EmbeddedId Annotation (version 2)");
        final IBookKey ik = new IBookKey();
        ik.setPublisherId(1);
        iBook.setKey(ik);
        em.persist(iBook);

        em.flush();
        provider.commitTransaction();


        assertEquals(
                "Composed Generated Id in Entity using EmbeddedId Annotation",
                book.getTitle()
        );
    }
}