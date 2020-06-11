package io.costax.model;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProjectTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void direct_fetching() {
        provider.doIt(em -> {

            /**
             * The easiest way to load an entity is to call the find method of the Java Persistence EntityManager
             * interface.
             */
            final Project project1 = em.find(Project.class, 1L);

            /**
             * The same can be achieved with the Hibernate native API:
             */
            final Project project2 = em.unwrap(Session.class).get(Project.class, 2L);
        });
    }

}