package io.costax.relationships.onetomany;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OneToManyBidirectionalTest {

    @PersistenceContext
    public EntityManager em;

    @Test
    public void test() {

        em.getTransaction().begin();

        final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
        em.persist(quentinTarantino);

        final Movie pulpFiction = Movie.of(2, "Pulp Fiction", quentinTarantino);

        pulpFiction.addReview("The book Vincent reads throughout most of the film is ...");
        pulpFiction.addReview("Vincent shoots Marvin with a M1911A1 pistol. :)");

        em.persist(pulpFiction);

        pulpFiction.addReview("I know that's what I always say. I'm always right, too.");

        em.getTransaction().commit();

    }
}
