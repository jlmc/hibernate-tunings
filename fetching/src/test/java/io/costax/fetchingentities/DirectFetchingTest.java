package io.costax.fetchingentities;

import io.costax.model.Client;
import io.costax.model.Issue;
import io.costax.model.Project;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.proxy.HibernateProxy;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@JpaTest(persistenceUnit = "it")
public class DirectFetchingTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectFetchingTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void direct_fetching() {
        final EntityManager em = provider.em();

        /*
         * The easiest way to load an entity is to call the find method of the Java Persistence EntityManager
         * interface.
         */
        final Project project1 = em.find(Project.class, 1L);

        /*
         * The same can be achieved with the Hibernate native API:
         */
        final Project project2 = em.unwrap(Session.class).get(Project.class, 2L);

        em.close();
    }

    @Test
    public void fetching_a_proxy_reference() {
        final EntityManager em = provider.em();

        /*
         * Alternatively, direct fetching can also be done lazily. For this purpose, the EntityManager must
         * return a Proxy which delays the SQL query execution until the entity is accessed for the first
         * time.
         */
        final Project reference = em.getReference(Project.class, 1L);

        LOGGER.info("Loaded Project entity");
        LOGGER.info("The project title is '{}'", reference.getTitle());

        /*
         * The getReference method call does not execute the SQL statement right away, so the Loaded
         * post entity message is the first to be logged. When the Post entity is accessed by calling the
         * getTitle method, Hibernate executes the select query and, therefore, loads the entity prior to
         * returning the title attribute.
         */

        em.close();
    }

    @Test
    public void fetching_a_proxy_reference_with_hibernate_api() {
        final EntityManager em = provider.em();

        /*
         * The same effect can be achieved with the Hibernate native API which offers two alternatives
         * for fetching an entity Proxy:
         */

        Session session = em.unwrap(Session.class);
        Project project2 = session.byId(Project.class).getReference(2L);

        LOGGER.info("Loaded Project entity");
        LOGGER.info("The project title is '{}'", project2.getTitle());

        Project project3 = session.load(Project.class, 3L);

        LOGGER.info("Loaded Project entity");
        LOGGER.info("The project title is '{}'", project3.getTitle());

        em.close();
    }

    @Test
    public void populating_a_child_side_parent_association() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Project project1 = em.getReference(Project.class, 1L);

        final Issue newProject1Issue = Issue.of(project1, "populating a child side parent association", "If the current Persistence Context does not require to load the parent entity, the aforementioned select statement will be a waste of resources. For this purpose, the getReference\n" +
                "method allows populating the parent attribute with a Proxy which Hibernate can use to set\n" +
                "the underlying foreign key value even if the Proxy is uninitialized.");

        em.persist(newProject1Issue);

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void natural_identifier_fetching() {
        final EntityManager em = provider.em();

        Session session = em.unwrap(Session.class);
        Client client = session.bySimpleNaturalId(Client.class).load("ptech");

        em.close();

        assertNotNull(client);
    }

    @Test
    public void natural_identifier_proxy_fetching() {
        final EntityManager em = provider.em();

        Session session = em.unwrap(Session.class);
        var persistentInstanceOrProxy = session.bySimpleNaturalId(Client.class).getReference("feedit");

        assertNotNull(persistentInstanceOrProxy);

        final boolean initialized = Hibernate.isInitialized(persistentInstanceOrProxy);
        final boolean isAProxy = HibernateProxy.class.isAssignableFrom(persistentInstanceOrProxy.getClass());

        LOGGER.info("is initialized [{}]", initialized);
        LOGGER.info("is a proxy [{}]", isAProxy);

        em.close();
    }
}
