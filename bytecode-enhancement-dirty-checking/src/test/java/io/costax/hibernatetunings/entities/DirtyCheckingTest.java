package io.costax.hibernatetunings.entities;

import io.costax.rules.EntityManagerProvider;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.sql.Statement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DirtyCheckingTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");


    @After
    @Before
    public void before() {
        provider.beginTransaction();
        final Session session = provider.em().unwrap(Session.class);

        session.doWork(connection -> {

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("delete from public.article");
            } catch (Exception ignore) {
            }
        });

        provider.commitTransaction();
    }


    @Test
    public void testDirtyChecking() {

        // First Transaction
        {
            provider.beginTransaction();

            final Article article = Article.of(1, "JPA-with-Hibernate");
            provider.em().persist(article);

            provider.commitTransaction();
        }


        // Second Transaction
        {
            provider.beginTransaction();

            final Article article = provider.em().find(Article.class, 1);
            article.setName("Hibernate Enhance Dirty Checking Mechanism");

            provider.em().flush();

            provider.commitTransaction();
        }
    }


    @Test
    public void zhouldRemoveAllData() {
        provider.beginTransaction();

        final Session session = provider.em().unwrap(Session.class);

        session.createNativeQuery(
                "delete from article where id > 0")
                .setFlushMode(FlushMode.ALWAYS)
                .addSynchronizedEntityClass(Article.class)
                .executeUpdate();

        provider.commitTransaction();
    }
}