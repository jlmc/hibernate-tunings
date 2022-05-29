package io.costax.hibernatetunning.persistencecontext;

import io.costax.hibernatetunings.entities.blog.Dashboard;
import io.costax.hibernatetunings.entities.blog.Post;
import io.costax.hibernatetunings.entities.blog.TopicStatistic;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JPAAutoFlushTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    @Order(1)
    public void flush_auto_jpql() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Long jpqlCountResultBeforePersists = em.createQuery("select count(p) from Dashboard p", Long.class).getSingleResult();
        assertEquals(0L, jpqlCountResultBeforePersists);


        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        em.persist(dashboard);


        final Long jpqlCountResulAfterPersists = em.createQuery("select count(p) from Dashboard p", Long.class).getSingleResult();
        assertEquals(1L, jpqlCountResulAfterPersists);

        em.getTransaction().rollback();
        em.close();
    }

    @Test
    @Order(2)
    public void flush_auto_jpql_tablespace_overlap() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Long jpqlCountResultBeforePersists = em.createQuery("select count(p) from Post p", Long.class).getSingleResult();
        assertEquals(0L, (long) jpqlCountResultBeforePersists);

        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        dashboard.addTopic(new Post("Jc", "flush-mode", "nothing waait a moment..."));
        em.persist(dashboard);

        final List<TopicStatistic> staticts = em.createQuery("select s from TopicStatistic s join fetch s.topic", TopicStatistic.class).getResultList();


        final Long jpqlCountResultAfterPersists = em.createQuery("select count(p) from Post p", Long.class).getSingleResult();
        assertEquals(1L, (long) jpqlCountResultAfterPersists);

        em.getTransaction().rollback();
        em.close();
    }

    @Test
    public void c_testFlushAutoNativeSQL() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        final Number jpqlCountResultBeforePersists = (Number) em.createNativeQuery("select count(p.id) from Post p").getSingleResult();
        assertEquals(0L, jpqlCountResultBeforePersists.longValue());

        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        dashboard.addTopic(new Post("Jc", "flush-mode", "nothing waait a moment..."));
        em.persist(dashboard);


        final List<Tuple> staticts = em.createNativeQuery("select s.* from topic_statistics s inner join topic t on s.topic_id = t.id", Tuple.class).getResultList();

        assertNotNull(staticts);

        final Number jpqlCountResultAfterPersists = (Number) em.createNativeQuery("select count(p.id) from Post p").getSingleResult();
        assertEquals(1L, jpqlCountResultAfterPersists.longValue());

        em.getTransaction().rollback();
        em.close();
    }
}
