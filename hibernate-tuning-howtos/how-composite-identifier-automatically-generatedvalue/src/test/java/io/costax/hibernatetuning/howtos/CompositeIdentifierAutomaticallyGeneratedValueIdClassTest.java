package io.costax.hibernatetuning.howtos;

import io.costax.hibernatetuning.howtos.book.Book;
import io.costax.hibernatetuning.howtos.book.PK;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The Current Book IdClass(PK.class) mapping only works if all the generated values are sequences.
 * Otherwise we should use a @EmbeddedId mapping strategy.
 */
@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from public.book where true", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from public.book where true", phase = Sql.Phase.AFTER_TEST_METHOD)
public class CompositeIdentifierAutomaticallyGeneratedValueIdClassTest {

    @PersistenceContext
    public EntityManager em;

    @Test
    public void should_persist_entity_with_composed_generated_key() {
        em.getTransaction().begin();

        Book book = Book.of(1, "Building Microservices");
        em.persist(book);

        Book book2 = Book.of(1, "Building Microservices version 2");
        em.persist(book2);

        em.flush();
        em.getTransaction().commit();

        PK key = new PK(book2.getRegistrationNumber(), 1);
        Book book2Fetched = em.find(Book.class, key);

        assertEquals(
                "Building Microservices version 2",
                book2Fetched.getTitle()
        );
    }
}