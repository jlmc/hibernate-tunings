package io.costax.hibernatetunig.joinunrelatedentities;

import io.costax.hibernatetunig.model.Article;
import io.costax.hibernatetunig.model.Project;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.sql.Statement;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JoinUnrelatedEntitiesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    public void t0_populate() {
        final EntityManager em = provider.em();
        provider.beginTransaction();

        deleteAllProjectsAndArticles(em);

        final Project joinUnrelatedEntitiesProject = Project.of(1L, "Join Unrelated Entities");
        em.persist(joinUnrelatedEntitiesProject);

        final Article joinUnrelatedEntitiesArticle = Article.of(2L, "Join Unrelated Entities");
        em.persist(joinUnrelatedEntitiesArticle);

        provider.commitTransaction();
    }

    @After
    public void t0_cleanup() {
        final EntityManager em = provider.createdEntityManagerUnRuled();
        em.getTransaction().begin();

        deleteAllProjectsAndArticles(em);

        em.getTransaction().commit();
    }

    private void deleteAllProjectsAndArticles(final EntityManager em) {
        final Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
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
    public void t1_join_unrelated_entities_with_jpa_2_1_and_hibernate_older_than_5_1() {
        final EntityManager em = provider.em();

        //List<Object[]> results = em.createQuery("select p.id, p.title, p.version, a.id, a.name from Project p, Article a where a.name = p.title and p.id = :pid")
        List<Object[]> results = em.createQuery("select p, a from Project p, Article a where a.name = p.title and p.id = :pid", Object[].class)
                .setParameter("pid", 1L)
                .getResultList();

        assertResults(results);
    }


    /**
     * Hibernate 5.1 introduced explicit joins on unrelated entities.
     * The syntax and behaviour are similar to SQL JOIN statements as you can see in the following example code .
     * Instead of referencing the attribute which defines the relationship between the two entities, you have to reference the second entity which you want to join and define the join criteria in the ON part of the statement.
     */
    @Test
    public void t1_join_unrelated_entities_with_jpa_2_2_and_hibernate_5_1() {
        final EntityManager em = provider.em();

        List<Object[]> results = em.createQuery("select p, a " +
                "from Project p join Article a on (a.name = p.title) " +
                "where p.id = :pid", Object[].class)
                .setParameter("pid", 1L)
                .getResultList();

        assertResults(results);
    }

    @Test
    public void t1_outer_join_unrelated_entities_with_jpa_2_2_and_hibernate_5_1() {
        final EntityManager em = provider.em();

        List<Object[]> results = em.createQuery(
                "select p, a from Project p " +
                        "left join Article a on (a.name = p.title and a.id <> p.id) " +
                        "where p.id = :pid", Object[].class)
                .setParameter("pid", 1L)
                .getResultList();

        assertResults(results);
    }

    private void assertResults(final List<Object[]> results) {
        assertThat(results, hasSize(1));
        final Object[] objects = results.get(0);
        assertThat(objects, notNullValue());
        assertThat(objects[0], notNullValue());
        assertThat(objects[1], notNullValue());

        Project project = (Project) objects[0];
        Article article = (Article) objects[1];

        assertThat(project, notNullValue());
        assertThat(article, notNullValue());

        assertThat(project, hasProperty("id", is(1L)));
        assertThat(project, hasProperty("title", is("Join Unrelated Entities")));
        assertThat(project, hasProperty("version", is(0)));

        assertThat(article, hasProperty("id", is(2L)));
        assertThat(article, hasProperty("name", is("Join Unrelated Entities")));
    }
}