package io.costax.relationships.manytomany;

import io.costax.relationships.onetomany.Actor;
import io.costax.relationships.onetomany.Director;
import io.costax.relationships.onetomany.Movie;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManyToManyAlternativeOneToManyTest {

    @JpaContext
    public JpaProvider provider;

    private static final Logger LOGGER = LoggerFactory.getLogger(ManyToManyAlternativeOneToManyTest.class);

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

            @SuppressWarnings("SqlResolve")
            final Integer johnTravoltaEntry = (Integer)
                    em.createNativeQuery("select entry from MOVIE_ACTOR_PERSONAGE where actor_id = :_id")
                    .setParameter("_id", johnTravolta.getId())
                    .getSingleResult();

            LOGGER.info("****** Check the JohnTravolta Entry: [{}] ", johnTravoltaEntry);

            em.getTransaction().begin();

            // Alter remove sql command executed the order entry hidden column must be updated
            pulpFiction.removePersonage(johnTravolta);

            em.flush();

        });

        provider.doIt(em -> {

            final List<MovieActorPersonage> personages =
                    em.createQuery(
                    """
                    select ma 
                    from MovieActorPersonage ma 
                    where ma.movie.id = :_movie
                    """, MovieActorPersonage.class)
                    .setParameter("_movie", 2)
                    .getResultList();

            LOGGER.info("Total [{}]", personages.size());
        });

    }
}
