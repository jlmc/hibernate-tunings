package io.costax.hibernatetuning.howtos;

import io.costax.hibernatetuning.howtos.ibook.IBook;
import io.costax.hibernatetuning.howtos.ibook.IBookKey;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * This mapping is useful for databases that do not support sequences
 */
@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from public.book where true", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from public.book where true", phase = Sql.Phase.AFTER_TEST_METHOD)
public class CompositeIdentifierAutomaticallyGeneratedValueEmbeddedIdTest {

    @PersistenceContext
    public EntityManager em;


    @Test
    public void should_persist_entity_with_composed_generated_key() {

        em.getTransaction().begin();


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

        em.getTransaction().commit();


        Assertions.assertEquals(
                "Composed Generated Id in Entity using EmbeddedId Annotation",
                book.getTitle()
        );
    }
}