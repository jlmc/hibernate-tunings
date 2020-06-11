package io.costax.relationships.manytoone;

import io.costax.relationships.onetomany.Director;
import io.costax.relationships.onetomany.Movie;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import javax.persistence.EntityGraph;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManyToOneTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void persist_movie_with_a_existing_director() {
        provider.doInTx(em -> {

            final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
            em.persist(quentinTarantino);

            final Movie djangoUnchained = Movie.of(1, "Django Unchained", quentinTarantino);
            em.persist(djangoUnchained);

        });


        provider.doIt(em -> {

            final EntityGraph<Movie> entityGraph = em.createEntityGraph(Movie.class);
            entityGraph.addAttributeNodes("director");

            final Map<String, Object> hints = Map.of("javax.persistence.loadgraph", entityGraph);

            //  LockModeType.OPTIMISTIC_FORCE_INCREMENT

            final Movie movie = em.find(Movie.class, 1, hints);

            assertNotNull(movie);
            assertEquals(Director.of(1, "Quentin Tarantino"), movie.getDirector());
        });
    }
}