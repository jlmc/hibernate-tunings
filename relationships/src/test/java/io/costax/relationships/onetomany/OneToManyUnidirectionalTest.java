package io.costax.relationships.onetomany;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OneToManyUnidirectionalTest {

    @JpaContext
    public JpaProvider provider;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    @Order(0)
    public void test_insert() {
        provider.doInTx(em -> {

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
        });
    }

    @Test
    @Order(1)
    public void test_fetch() {
        provider.doIt(em -> {

            final Movie movie = em.find(Movie.class, 1111);

            assertEquals("Maradona by Kusturica", movie.getTitle());

            logger.info("***** LOAD THE SCENES LAZY ******");

            final Set<Scene> scenes = movie.getScenes();

            assertEquals(5, scenes.size());

            scenes.forEach(scene -> logger.info("----- [{} - {}] ", scene.getLi(), scene.getDescription()));
        });
    }

    @Test
    @Order(2)
    public void test_add_one() {
        provider.doInTx(em -> {

            final Movie movie = em.find(Movie.class, 1111);
            movie.addScene(Scene.of(6, "What about Messi?"));

        });
    }

    @Test
    @Order(3)
    public void throw_constrains_exception_removing_a_movie_with_jpql_when_the_movie_id_is_used_in_other_tables_as_fk() {
        provider.doInTx(em -> {
            try {

                em.createQuery("delete from Movie m where m.id = :_id")
                        .setParameter("_id", 1111)
                        .executeUpdate();

                fail("Should not be here, The movie 1111 contains scenes, Using JPQL or SQL before we remove a movie we have to remove all the dependent object");

            } catch (javax.persistence.PersistenceException e) {
                logger.warn("The movie 1111 contains scenes, Using JPQL or SQL before we remove a movie we have to remove all the dependent object");
            }
        });
    }

    @Test
    @Order(4)
    public void remove_a_movie_with_jpql() {
        provider.doInTx(em -> {
            em.createQuery(
                    "delete from Scene s where s in (" +
                            "select s from Movie m inner join m.scenes s where m.id = :_id)")
                    .setParameter("_id", 1111)
                    .executeUpdate();

            em.createQuery("delete from Movie m where m.id = :_id")
                    .setParameter("_id", 1111)
                    .executeUpdate();

        });
    }
}
