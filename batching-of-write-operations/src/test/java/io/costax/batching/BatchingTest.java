package io.costax.batching;

import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class BatchingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatchingTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Test
    public void testInsertActors() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 1; i <= 20; i++) {
            Actor actor = Actor.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            if (i % 5 == 0) {
                em.flush();
                em.clear();
            }
        }

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testInsertActorsSerie() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        for (int i = 0; i < 10; i++) {
            Actor actor = Actor.of("First-Name-" + i, "LastName" + i);

            em.persist(actor);

            io.costax.batching.Serie b = Serie.of("Title-" + i, "Description-" + 1);
            b.addActor(actor);

            em.persist(b);
        }

        em.getTransaction().commit();
        em.close();
    }


    @Test
    public void testUpdateActorsBooks() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        List<Actor> authors = em
                .createQuery(
                        "SELECT a FROM Actor a JOIN FETCH a.series b",
                        Actor.class).getResultList();

        for (Actor a : authors) {
            a.setFirstName(a.getFirstName() + " - updated");

            a.getSeries().forEach(b -> b.setTitle(b.getTitle() + " - updated"));
        }

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testDeleteActorsWithoutBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        List<Actor> actors = em.createQuery("SELECT a FROM Actor a", Actor.class).getResultList();

        for (Actor a : actors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }


    @Test
    public void testDeleteActorsWithBatch() {

        EntityManager em = provider.em();
        em.getTransaction().begin();

        em.createQuery("DELETE Serie b").executeUpdate();

        List<Actor> actors = em.createQuery("SELECT a FROM Actor a LEFT JOIN FETCH a.series", Actor.class).getResultList();

        for (Actor a : actors) {
            em.remove(a);
        }

        em.getTransaction().commit();
        em.close();
    }
}
