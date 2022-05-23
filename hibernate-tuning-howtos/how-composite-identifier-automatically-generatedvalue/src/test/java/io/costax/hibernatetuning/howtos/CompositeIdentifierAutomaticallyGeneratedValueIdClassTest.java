package io.costax.hibernatetuning.howtos;

import io.costax.hibernatetuning.howtos.book.Book;
import io.costax.hibernatetuning.howtos.book.PK;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
