package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

public class OneToManyBidirectionalTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void test() {

        final EntityManager em = provider.em();
        provider.beginTransaction();

        final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
        em.persist(quentinTarantino);

        final Movie pulpFiction = Movie.of(2, "Pulp Fiction", quentinTarantino);

        pulpFiction.addReview("The book Vincent reads throughout most of the film is ...");
        pulpFiction.addReview("Vincent shoots Marvin with a M1911A1 pistol. :)");

        em.persist(pulpFiction);

        pulpFiction.addReview("I know that's what I always say. I'm always right, too.");

        provider.commitTransaction();

    }
}
