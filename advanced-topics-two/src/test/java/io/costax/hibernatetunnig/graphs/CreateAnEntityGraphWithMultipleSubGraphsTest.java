package io.costax.hibernatetunnig.graphs;

import io.costax.hibernatetunnig.graphs.entity.Author;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

/**
 * Example of how to use SubGraphs inside SubGraphs.
 *
 * An EntityGraph provides an excellent way to avoid N+1 select issues
 * bu initializing the required lazily fetched associations.
 *
 * The Definitions of the graph is independent of the Query and defines which associations
 * JPA should initialize before returning our query result
 */
public class CreateAnEntityGraphWithMultipleSubGraphsTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void name() {
        final EntityManager em = provider.em();

        EntityGraph<?> graph = em.createEntityGraph("graph.AuthorBooksPublisherEmployee");
        TypedQuery<Author> q = em.createQuery("SELECT a FROM Author a WHERE a.id = 100", Author.class);
        q.setHint("javax.persistence.fetchgraph", graph);
        Author a = q.getSingleResult();

    }
}
