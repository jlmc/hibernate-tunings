package io.costax.model;

import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class ProjectTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void direct_fetching() {
        final EntityManager em = provider.em();

        /**
         * The easiest way to load an entity is to call the find method of the Java Persistence EntityManager
         * interface.
         */
        final Project project1 = em.find(Project.class, 1L);

        /**
         * The same can be achieved with the Hibernate native API:
         */
        final Project project2 = em.unwrap(Session.class).get(Project.class, 2L);
    }

}