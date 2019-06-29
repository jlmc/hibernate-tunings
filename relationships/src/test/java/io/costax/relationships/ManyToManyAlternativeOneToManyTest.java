package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ManyToManyAlternativeOneToManyTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void test() {

        provider.doInTx(em -> {

            final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
            em.persist(quentinTarantino);

            final Movie pulpFiction = Movie.of(2, "Pulp Fiction", quentinTarantino);
            //pulpFiction.addReview("The book Vincent reads throughout most of the film is ...");
            //pulpFiction.addReview("Vincent shoots Marvin with a M1911A1 pistol. :)");

            em.persist(pulpFiction);

            final Actor johnTravolta = Actor.of(1, "John Travolta");
            final Actor samuelLJackson = Actor.of(2, "Samuel L. Jackson");
            final Actor umaThurman = Actor.of(3, "Uma Thurman");

            em.persist(johnTravolta);
            em.persist(samuelLJackson);
            em.persist(umaThurman);

            pulpFiction.addPersonage(johnTravolta, "Vincent Vega");
            pulpFiction.addPersonage(samuelLJackson, "Jules Winnfield");
            pulpFiction.addPersonage(umaThurman, "Mia Wallace");

            em.flush();

            em.getTransaction().commit();

            final Integer johnTravoltaEntry = (Integer) em.createNativeQuery(
                    "select entry from MOVIE_ACTOR_PERSONAGE where actor_id = :_id"
                    )
                    .setParameter("_id", johnTravolta.getId())
                    .getSingleResult();

            logger.info("****** Check the JohnTravolta Entry: [{}] ", johnTravoltaEntry);

            em.getTransaction().begin();

            // Alter remove sql command executed the order entry hidden column must be updated
            pulpFiction.removePersonage(johnTravolta);

            em.flush();

        });

        provider.doIt(em -> {

            final List<MovieActorPersonage> personages = em.createQuery("select ma from MovieActorPersonage ma where ma.movie.id = :_movie", MovieActorPersonage.class)
                    .setParameter("_movie", 2)
                    .getResultList();

            logger.info("Total [{}]", personages.size());
        });

    }
}
