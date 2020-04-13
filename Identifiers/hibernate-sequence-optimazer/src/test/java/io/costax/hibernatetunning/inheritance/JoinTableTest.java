package io.costax.hibernatetunning.inheritance;

import io.costax.hibernatetunings.entities.blog.*;
import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JoinTableTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void t01_get_all_post_wi_th_jpql() {
        final List<Topic> topics = provider.em().createQuery("select t from Topic t", Topic.class).getResultList();
    }

    @Test
    public void t02_should_create_a_dashboard() {
        provider.beginTransaction();

        final EntityManager em = provider.em();
        final Dashboard jpa = Dashboard.of("JPA");

        final Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now().plusMonths(1));

        final Post post = Topics.newPostWith("John Doe", "Inheritance", "Best practices");
        jpa.addTopic(post);


        final Announcement announcement = Topics.newAnnouncementWith("John Doe", "Release x.y.z.Final", LocalDate.of(2019, 1, 31));
        jpa.addTopic(announcement);
        em.persist(jpa);

        final TopicStatistic postTopicStatistic = Topics.topicStatistics(post);
        em.persist(postTopicStatistic);

        final TopicStatistic announcementTopicStatistic = Topics.topicStatistics(announcement);
        em.persist(announcementTopicStatistic);

        provider.commitTransaction();
    }

    @Test
    public void t03_should_get_topic_of_some_dashboard_with_a_polymorphic_query() {
        final EntityManager em = provider.em();

        final Dashboard dashboard = em.createQuery("select b from Dashboard b where b.name = :name", Dashboard.class)
                .setParameter("name", "JPA")
                .getSingleResult();

        final List<Topic> topics = em.createQuery("select t from Topic t where t.dashboard = :dashboard", Topic.class)
                .setParameter("dashboard", dashboard)
                .getResultList();

        assertEquals(2, topics.size());
    }

    @Test
    public void t04_should_get_posts_with_subclass_query() {
        final EntityManager em = provider.em();

        final Dashboard dashboard = em.createQuery("select b from Dashboard b where b.name = :name", Dashboard.class)
                .setParameter("name", "JPA")
                .getSingleResult();


        List<Post> posts = em
                .createQuery(
                        "select p " +
                                "from Post p " +
                                "where p.dashboard = :dashboard", Post.class)
                .setParameter("dashboard", dashboard)
                .getResultList();

        assertEquals(1, posts.size());
    }

    @Test
    public void t05_should_fetch_topic_projection() {
        final Dashboard dashboard = provider.em()
                .createQuery("select b from Dashboard b where b.name = :name", Dashboard.class)
                .setParameter("name", "JPA")
                .getSingleResult();

        List<String> titles = provider.em()
                .createQuery("select t.title from Topic t where t.dashboard = :board", String.class)
                .setParameter("board", dashboard)
                .getResultList();

        assertEquals(2, titles.size());
    }

    @Test
    //@Ignore
    public void t06_should_fetch_just_one_topic() {
        final List<Long> allTopicsIds = provider.em().createQuery("select distinct id from Topic order by id desc", Long.class).getResultList();

        assertThat(allTopicsIds, not(empty()));

        final Long topicId = allTopicsIds.get(0);

        final Topic topic = provider.em().find(Topic.class, topicId);
        assertNotNull(topic);
    }

    @Test
    public void t07_fetch_dashboard_topics_eagerly() {
        final List<Dashboard> dashboards = provider.em()
                .createQuery(
                        "select distinct d from Dashboard d left join fetch d.topics where d.name = :name"
                        , Dashboard.class)
                .setParameter("name", "JPA")
                .getResultList();

        assertEquals(1, dashboards.size());
        final Dashboard dashboard = dashboards.get(0);

        assertTrue(dashboard.getTopics().stream().anyMatch(t -> t instanceof Post));
        assertTrue(dashboard.getTopics().stream().anyMatch(t -> t instanceof Announcement));
    }

    @Test
    public void t08_should_fetch_statistics() {
        final List<TopicStatistic> topicStatistics = provider.em().createQuery("select s from TopicStatistic s", TopicStatistic.class).getResultList();
        assertEquals(2, topicStatistics.size());
    }

    @Test
    public void t09_should_get_tuple_projection() {
        final EntityManager em = provider.em();

        List<Tuple> results = em
                .createQuery(
                        "select count(t), t.class " +
                                "from Topic t " +
                                "group by t.class " +
                                "order by t.class ")
                .getResultList();

        assertEquals(2, results.size());
    }

    @Test
    public void t10_should_order_the_the_types() {
        final EntityManager em = provider.em();

        final Dashboard dashboard = em.createQuery("select b from Dashboard b where b.name = :name", Dashboard.class)
                .setParameter("name", "JPA")
                .getSingleResult();

        List<Topic> topics = em
                .createQuery(
                        "select t " +
                                "from Topic t " +
                                "where t.dashboard = :dashboard " +
                                "order by " +
                                "   case " +
                                "   when type(t) = Announcement then 10" +
                                "   when type(t) = Post then 20 " +
                                "   end", Topic.class)
                .setParameter("dashboard", dashboard)
                .getResultList();

        assertEquals(2, topics.size());
        assertTrue(topics.get(0) instanceof Announcement);
        assertTrue(topics.get(1) instanceof Post);
    }

    @Test
    public void t11_select_using_all() {
        final EntityManager em = provider.em();

        final Dashboard dashboard = em.createQuery("select b from Dashboard b where b.name = :name", Dashboard.class)
                .setParameter("name", "JPA")
                .getSingleResult();


        List<Dashboard> postOnlyBoards = em
                .createQuery(
                        "select distinct b " +
                                "from Dashboard b " +
                                "where Post = all (" +
                                "   select type(t) from Topic t where t.dashboard = b" +
                                ")", Dashboard.class)
                .getResultList();
        assertEquals(0, postOnlyBoards.size());
        //assertEquals("JPA", postOnlyBoards.get(0).getName());

    }

    @Test
    public void t12_remove_all() {
        provider.beginTransaction();

        final EntityManager em = provider.em();

        final List<Dashboard> dashboards = em.createQuery("select distinct d from Dashboard d", Dashboard.class).getResultList();

        for (Dashboard dashboard : dashboards) {

            final List<Topic> topics = dashboard.getTopics();

            for (Topic topic : topics) {
                final List<TopicStatistic> topicStatistics = em.createQuery("select s from TopicStatistic s where topic = :topic", TopicStatistic.class).setParameter("topic", topic).getResultList();
                topicStatistics.forEach(em::remove);
            }

            em.remove(dashboard);
        }

        provider.commitTransaction();
    }
}
