package io.costax.queryhintfetchsize;

import io.costax.model.Project;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hamcrest.Matchers;
import org.hibernate.jpa.QueryHints;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;

public class QueryHintFetchSizeTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryHintFetchSizeTest.class);

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    /**
     * Considering the previous pagination query, the page size being also 10, the default fetch size does not influence the number of database roundtrips. However, if the page size is 50, then Hibernate will require 5 roundtrips to fetch the entire ResultSet.
     * <p>
     * Luckily, Hibernate can control the fetch size either on a query basis or at the EntityManagerFactory level.
     * <p>
     * At the query level, the fetch size can be configured using the org.hibernate.fetchSize hint:
     */
    @Test
    public void using_hint_query_fetch_size() {
        EntityManager em = provider.em();

        int pageStart = 0;
        int pageSize = 50;

        List<Project> projects = em.createQuery(
                "select p from Project p ", Project.class)
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .setHint(QueryHints.HINT_FETCH_SIZE, pageSize)
                .getResultList();

        Assert.assertThat(projects, Matchers.hasSize(pageSize));
    }
}
