package io.costax.hibernatetunig.model;

import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SecondaryTableOneToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    @After
    public void cleanup() {
        final EntityManager em = provider.createdEntityManagerUnRuled();
        em.getTransaction().begin();

        final Session session = em.unwrap(Session.class);
        session.doWork(connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("delete from tasks.todo_details");
                statement.executeUpdate("delete from tasks.todo");
            } catch (Exception ignore) {
            }
        });

        em.getTransaction().commit();
    }

    @Test
    public void t0_createSomeTodo() {

        provider.doInTx(em -> {
            final OffsetDateTime lisbonDate = OffsetDateTime.of(2018, 2, 10, 17, 20, 0, 0, ZoneOffset.UTC);

            final Todo slb = Todo.of(1L, "Win 10-Zero", lisbonDate, "SLB");

            provider.em().persist(slb);
        });

        provider.doIt(em -> {
            final OffsetDateTime lisbonDate = OffsetDateTime.of(2018, 2, 10, 17, 20, 0, 0, ZoneOffset.UTC);
            final Todo expected = Todo.of(1L, "Win 10-Zero", lisbonDate, "SLB");

            final Todo todo = provider.em().find(Todo.class, 1L);

            Assert.assertThat(todo, Matchers.notNullValue());
            Assert.assertThat(todo, Matchers.samePropertyValuesAs(expected));
        });

        provider.beginTransaction();
        final EntityManager em = provider.em();
        final Todo todo = em.getReference(Todo.class, 1L);
        em.remove(todo);
        provider.commitTransaction();
    }

}
