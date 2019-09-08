package io.costax.relationships.onetomany;

import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.Set;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OneToManyUnidirectionalTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void t00__testInsert() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
        em.persist(quentinTarantino);

        final Movie pulpFiction = Movie.of(2, "Pulp Fiction", quentinTarantino);
        //pulpFiction.addReview("The book Vincent reads throughout most of the film is ...");
        //pulpFiction.addReview("Vincent shoots Marvin with a M1911A1 pistol. :)");

        em.persist(pulpFiction);

        pulpFiction.addScene(Scene.of(1, "Prologue – The Diner"));
        pulpFiction.addScene(Scene.of(2, "Prelude to \"Vincent Vega and Marsellus Wallace's Wife\""));
        pulpFiction.addScene(Scene.of(3, "Vincent Vega and Marsellus Wallace's Wife"));
        pulpFiction.addScene(Scene.of(4, "Prelude to \"The Gold Watch\""));
        pulpFiction.addScene(Scene.of(5, "The Gold Watch"));
        pulpFiction.addScene(Scene.of(6, "The Bonnie Situation"));
        pulpFiction.addScene(Scene.of(7, "Epilogue – The Diner"));

        em.flush();
        em.getTransaction().commit();
    }

    @Test
    public void t01__testFetch() {
        final EntityManager em = provider.em();

        final Movie movie = em.find(Movie.class, 1111);

        Assert.assertEquals("Maradona by Kusturica", movie.getTitle());

        logger.info("***** LOAD THE SCENES LAZY ******");

        final Set<Scene> scenes = movie.getScenes();

        Assert.assertEquals(5, scenes.size());

        scenes.forEach(scene -> logger.info("----- [{} - {}] ", scene.getLi(), scene.getDescription()));
    }

    @Test
    public void t02__testAddOne() {
        provider.doInTx(em -> {

            final Movie movie = em.find(Movie.class, 1111);
            movie.addScene(Scene.of(6, "What about Messi?"));

        });
    }

    @Test
    public void t04_removeAMovieWithJPQL() {
        provider.doInTx(em -> {
            try {

                em.createQuery("delete from Movie m where m.id = :_id")
                        .setParameter("_id", 1111)
                        .executeUpdate();

                Assert.fail("Should not be here, The movie 1111 contains scenes, Using JPQL or SQL before we remove a movie we have to remove all the dependent object");

            } catch (javax.persistence.PersistenceException e) {
                logger.warn("The movie 1111 contains scenes, Using JPQL or SQL before we remove a movie we have to remove all the dependent object");
            }
        });
    }

    @Test
    public void t05_removeAMovieWithJPQL() {
        provider.doInTx(em -> {
            try {

                em.createQuery(
                        "delete from Scene s where s in (" +
                                "select s from Movie m inner join m.scenes s where m.id = :_id)")
                        .setParameter("_id", 1111)
                        .executeUpdate();

                em.createQuery("delete from Movie m where m.id = :_id")
                        .setParameter("_id", 1111)
                        .executeUpdate();

            } catch (javax.persistence.PersistenceException e) {
                logger.warn("The movie 1111 contains scenes, Using JPQL or SQL before we remove a movie we have to remove all the dependent object");
            }
        });
    }
}
