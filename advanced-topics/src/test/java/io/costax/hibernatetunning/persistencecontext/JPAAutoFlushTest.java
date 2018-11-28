package io.costax.hibernatetunning.persistencecontext;

import io.costa.hibernatetunings.entities.blog.Dashboard;
import io.costa.hibernatetunings.entities.blog.Post;
import io.costa.hibernatetunings.entities.blog.TopicStatistic;
import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JPAAutoFlushTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_testFlushAutoJPQL() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Long jpqlCountResultBeforePersists = em.createQuery("select count(p) from Dashboard p", Long.class).getSingleResult();
        assertThat(jpqlCountResultBeforePersists, is(0L));


        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        em.persist(dashboard);


        final Long jpqlCountResulAfterPersists = em.createQuery("select count(p) from Dashboard p", Long.class).getSingleResult();
        assertThat(jpqlCountResulAfterPersists, is(1L));

        provider.rollbackTransaction();
    }

    @Test
    public void b_testFlushAutoJPQLTableSpaceOverlap() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Long jpqlCountResultBeforePersists = em.createQuery("select count(p) from Post p", Long.class).getSingleResult();
        assertThat(jpqlCountResultBeforePersists, is(0L));

        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        dashboard.addTopic(new Post("Jc", "flush-mode", "nothing waait a moment..."));
        em.persist(dashboard);


        final List<TopicStatistic> staticts = em.createQuery("select s from TopicStatistic s join fetch s.topic", TopicStatistic.class).getResultList();


        final Long jpqlCountResultAfterPersists = em.createQuery("select count(p) from Post p", Long.class).getSingleResult();
        assertThat(jpqlCountResultAfterPersists, is(1L));

        provider.rollbackTransaction();
    }

    @Test
    public void c_testFlushAutoNativeSQL() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        final Number jpqlCountResultBeforePersists = (Number) em.createNativeQuery("select count(p.id) from Post p").getSingleResult();
        assertThat(jpqlCountResultBeforePersists, is(BigInteger.valueOf(0L)));

        Dashboard dashboard = Dashboard.of("Jpa-advanced-topic");
        dashboard.addTopic(new Post("Jc", "flush-mode", "nothing waait a moment..."));
        em.persist(dashboard);


        final List<Tuple> staticts = em.createNativeQuery("select s.* from topic_statistics s inner join topic t on s.topic_id = t.id", Tuple.class).getResultList();

        assertThat(staticts, is(notNullValue()));

        final Number jpqlCountResultAfterPersists = (Number) em.createNativeQuery("select count(p.id) from Post p").getSingleResult();
        assertThat(jpqlCountResultAfterPersists, is(BigInteger.valueOf(1L)));

        provider.rollbackTransaction();
    }
}
