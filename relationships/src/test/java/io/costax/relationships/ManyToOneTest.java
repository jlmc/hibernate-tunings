package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.Map;

import static org.hamcrest.Matchers.is;

public class ManyToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void persistMovieWithAExistingDirector() {
        provider.doInTx(em -> {

            final Director quentinTarantino = Director.of(1, "Quentin Tarantino");
            em.persist(quentinTarantino);

            final Movie djangoUnchained = Movie.of(1, "Django Unchained", quentinTarantino);
            em.persist(djangoUnchained);

        });


        final EntityManager em = provider.em();

        final EntityGraph<Movie> entityGraph = em.createEntityGraph(Movie.class);
        entityGraph.addAttributeNodes("director");

        final Map<String, Object> hints = Map.of("javax.persistence.loadgraph", entityGraph);

        //  LockModeType.OPTIMISTIC_FORCE_INCREMENT

        final Movie movie = em.find(Movie.class, 1, hints);

        Assert.assertNotNull(movie);
        Assert.assertThat(movie.getDirector(), is(Director.of(1, "Quentin Tarantino")));
    }
}