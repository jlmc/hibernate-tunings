package io.costax.queryhintfetchsize;

import io.costax.model.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

@JpaTest(persistenceUnit = "it")
public class QueryHintFetchSizeTest {

    @PersistenceContext
    public EntityManager em;

    /**
     * Considering the previous pagination query, the page size being also 10, the default fetch size does not influence the number of database roundtrips. However, if the page size is 50, then Hibernate will require 5 roundtrips to fetch the entire ResultSet.
     * <p>
     * Luckily, Hibernate can control the fetch size either on a query basis or at the EntityManagerFactory level.
     * <p>
     * At the query level, the fetch size can be configured using the org.hibernate.fetchSize hint:
     */
    @Test
    public void using_hint_query_fetch_size() {

        int pageStart = 0;
        int pageSize = 50;

        List<Project> projects = em.createQuery(
                "select p from Project p ", Project.class)
                .setFirstResult(pageStart)
                .setMaxResults(pageSize)
                .setHint(QueryHints.HINT_FETCH_SIZE, pageSize)
                .getResultList();

        Assertions.assertEquals(pageSize, projects.size());
    }
}
