package io.costax.hibernatetunig.joinunrelatedentities;

import io.costax.hibernatetunig.model.Article;
import io.costax.hibernatetunig.model.Project;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class JoinUnrelatedEntitiesTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void populate() {
        provider.doInTx(em -> {
            deleteAllProjectsAndArticles(em);

            final Project joinUnrelatedEntitiesProject = Project.of(1L, "Join Unrelated Entities");
            em.persist(joinUnrelatedEntitiesProject);

            final Article joinUnrelatedEntitiesArticle = Article.of(2L, "Join Unrelated Entities");
            em.persist(joinUnrelatedEntitiesArticle);

        });
    }

    @AfterEach
    public void cleanup() {
        provider.doInTx(this::deleteAllProjectsAndArticles);
    }

    private void deleteAllProjectsAndArticles(final EntityManager em) {
        final Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("delete from public.issue");
                statement.executeUpdate("delete from public.project");
                statement.executeUpdate("delete from public.article");
            } catch (Exception ignore) {
            }
        });
    }

    /**
     * with JPA 2.1 and Hibernate 5.0 it’s still not possible to join two unrelated entities.
     * <p>
     * The only thing you can do is to reference both entities in the FROM part to create a cartesian product and reduce it in the WHERE part of the query.
     */
    @Test
    @Order(0)
    public void join_unrelated_entities_with_jpa_2_1_and_hibernate_older_than_5_1() {
        //List<Object[]> results = em.createQuery("select p.id, p.title, p.version, a.id, a.name from Project p, Article a where a.name = p.title and p.id = :pid")

        List<Object[]> results = provider.doItWithReturn(em ->
                em.createQuery("select p, a from Project p, Article a where a.name = p.title and p.id = :pid", Object[].class)
                        .setParameter("pid", 1L)
                        .getResultList());

        assertResults(results);
    }


    /**
     * Hibernate 5.1 introduced explicit joins on unrelated entities.
     * The syntax and behaviour are similar to SQL JOIN statements as you can see in the following example code .
     * Instead of referencing the attribute which defines the relationship between the two entities, you have to reference the second entity which you want to join and define the join criteria in the ON part of the statement.
     */
    @Test
    @Order(1)
    public void join_unrelated_entities_with_jpa_2_2_and_hibernate_5_1() {
        provider.em();

        List<Object[]> results = provider.doItWithReturn(em ->
                em.createQuery(
                        """
                                select p, a
                                from Project p join Article a on (a.name = p.title)
                                where p.id = :pid
                                """, Object[].class)
                        .setParameter("pid", 1L)
                        .getResultList());

        assertResults(results);
    }

    @Test
    @Order(2)
    public void outer_join_unrelated_entities_with_jpa_2_2_and_hibernate_5_1() {
        List<Object[]> results = provider.doItWithReturn(em ->
                em.createQuery(
                        """
                                select p, a 
                                from Project p 
                                left join Article a on (a.name = p.title and a.id <> p.id) 
                                where p.id = :pid
                                """, Object[].class)
                        .setParameter("pid", 1L)
                        .getResultList());

        assertResults(results);
    }

    private void assertResults(final List<Object[]> results) {
        assertTrue(results.size() == 1);
        final Object[] objects = results.get(0);
        assertNotNull(objects);
        assertNotNull(objects[0]);
        assertNotNull(objects[1]);

        Project project = (Project) objects[0];
        Article article = (Article) objects[1];

        assertNotNull(project);
        assertNotNull(article);

        assertEquals(1L, project.getId());
        assertEquals("Join Unrelated Entities", project.getTitle());
        assertEquals(0, project.getVersion());

        assertEquals(2L, article.getId());
        assertEquals("Join Unrelated Entities", article.getName());

    }
}