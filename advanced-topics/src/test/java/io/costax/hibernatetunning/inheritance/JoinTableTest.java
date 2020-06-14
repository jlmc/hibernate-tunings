package io.costax.hibernatetunning.inheritance;

import io.costax.hibernatetunings.entities.blog.*;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.junit.jupiter.api.Assertions.*;


@JpaTest(persistenceUnit = "it")
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)

public class JoinTableTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(JoinTableTest.class);
    public static final String DASHBOARD_TITLE_KEY = "JPA-Tutorial";
    @JpaContext
    public JpaProvider provider;
    private Long dashboardId;
    private Long announcementId;
    private Long postId;

    /**
     * Create a simple hieratic data that is mapped with InheritanceType
     */
    @BeforeEach
    void setUp() {

        LOGGER.info("====>>>> Before the test execution, Creating 1 dashboard with 2 topic of different types !!!");

        provider.doInTx(em -> {

            final Dashboard jpaBoard = Dashboard.of(DASHBOARD_TITLE_KEY);

            final Post post = Topics.newPostWith("kaser Soza", "Inheritance with InheritanceType.JOINED", "How to use InheritanceType.JOINED");
            jpaBoard.addTopic(post);

            final Announcement announcement = Topics.newAnnouncementWith("John Doe", "Release x.y.z.Final", LocalDate.of(2019, 1, 31));
            jpaBoard.addTopic(announcement);
            em.persist(jpaBoard);

            final TopicStatistic postTopicStatistic = Topics.topicStatistics(post);
            em.persist(postTopicStatistic);

            final TopicStatistic announcementTopicStatistic = Topics.topicStatistics(announcement);
            em.persist(announcementTopicStatistic);

            em.flush();

            // REMEMBER the
            dashboardId = jpaBoard.getId();
            announcementId = announcement.getId();
            postId = post.getId();

        });

        LOGGER.info("<<<<==== Before the test execution, Creating 1 dashboard with 2 topic of different types !!!");
    }

    @AfterEach
    void tearDown() {

        LOGGER.info("====>>>> After the test execution, Deleting all dashboards and its Topics !!!");

        provider.doInTx(em -> {

            final List<Dashboard> dashboards = em.createQuery("select distinct d from Dashboard d", Dashboard.class).getResultList();

            for (final Dashboard dashboard : dashboards) {
                final List<Topic> topics = dashboard.getTopics();

                for (Topic topic : topics) {
                    final List<TopicStatistic> topicStatistics = em.createQuery("select s from TopicStatistic s where topic = :topic", TopicStatistic.class).setParameter("topic", topic).getResultList();
                    topicStatistics.forEach(em::remove);
                }

                em.remove(dashboard);
            }
        });

        LOGGER.info("<<<<==== After the test execution, Deleting all dashboards and its Topics !!!");
    }

    /**
     * Remember that the jpql:
     *
     * - order by:
     *    type( i )  - use the column DiscriminatorValue
     *    i.class    - use the column DiscriminatorValue
     *
     * - In the projection :
     *   type(t) is the Class implementation
     *   i.class is the discriminator column value
     */

    @Test
    @Order(1)
    @DisplayName("Get all post with JPQL. The result is List of the basic Root type")
    public void get_all_post_with_JPQL() {

        LOGGER.info("===>>> Test execution ");

        final List<Topic> topics = provider.doItWithReturn(em -> em.createQuery("from Topic t order by type(t)", Topic.class).getResultList());

        assertNotNull(topics);
        assertFalse(topics.isEmpty());
        assertEquals(2, topics.size());
        assertTrue(topics.get(0) instanceof Post); // @DiscriminatorValue("0")
        assertTrue(topics.get(1) instanceof Announcement); // @DiscriminatorValue("1")

        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(2)
    @DisplayName("Get all post with JPQL. The result is List of the basic Root type")
    public void get_topic_of_some_dashboard_with_a_polymorphic_JPQL_query() {

        LOGGER.info("===>>> Test execution ");

        List<Topic> topics =
                provider.doItWithReturn(em ->

                        em.createQuery(
                                """
                                        select t 
                                        from Topic t 
                                        where t.dashboard = (
                                            select b from Dashboard b where b.name = :_name 
                                        )
                                        order by t.class
                                        """
                                , Topic.class)
                                .setParameter("_name", DASHBOARD_TITLE_KEY)
                                .getResultList()

                );

        // NOTE: The order by t.class also use the discriminator collumn value (    order by topic0_.type )

        assertEquals(2, topics.size());
        assertTrue(topics.get(0) instanceof Post); // @DiscriminatorValue("0")
        assertTrue(topics.get(1) instanceof Announcement); // @DiscriminatorValue("1")


        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(3)
    @DisplayName("Fetching the subtypes using JPQL subclass queries")
    void get_subtypes_with_JPQL_subclass_query() {

        LOGGER.info(">>>=== Test execution ");

        Map<Class<? extends Topic>, List<? extends Topic>> result =
                provider.doItWithReturn(em -> {

                    // select *
                    // from Post p
                    //   inner join topic t on p.id=t.id
                    // ...
                    final List<Post> posts = em.createQuery(
                            """
                                    select t 
                                    from Post t 
                                    where t.dashboard = (
                                        select b from Dashboard b where b.name = :_name 
                                    )
                                    order by t.id
                                    """
                            , Post.class)
                            .setParameter("_name", DASHBOARD_TITLE_KEY)
                            .getResultList();

                    // select *
                    // from from Announcement a
                    //   inner join topic t  on a.id=t.id
                    // ...
                    final List<Announcement> announcements = em.createQuery(
                            """
                                    select t 
                                    from Announcement t 
                                    where t.dashboard = (
                                        select b from Dashboard b where b.name = :_name 
                                    )
                                    order by t.id
                                    """
                            , Announcement.class)
                            .setParameter("_name", DASHBOARD_TITLE_KEY)
                            .getResultList();

                    return Map.of(Post.class, posts, Announcement.class, announcements);

                });


        LOGGER.info("<<<=== Test execution ");

        assertEquals(2, result.size());
        assertEquals(1, result.get(Post.class).size());
        assertEquals(1, result.get(Announcement.class).size());
    }


    @Test
    @Order(3)
    @DisplayName("Fetching topic property_projection_with_JPQL")
    void fetch_topic_property_projection_with_JPQL() {

        LOGGER.info(">>>=== Test execution ");

        List<String> result =
                provider.doItWithReturn(em -> {

                    // select t.title as col_0_0_
                    // from topic t
                    // where
                    //  t.dashboard_id=( 123 )
                    // order by t.type
                    // ...
                    return em.createQuery(
                            """
                                    select t.title 
                                    from Topic t 
                                    where t.dashboard = (
                                        select b from Dashboard b where b.name = :_name 
                                    )
                                    order by type (t)
                                    """
                            , String.class)
                            .setParameter("_name", DASHBOARD_TITLE_KEY)
                            .getResultList();


                });


        LOGGER.info("<<<=== Test execution ");

        assertEquals(2, result.size());
        assertEquals("Inheritance with InheritanceType.JOINED", result.get(0));
        assertEquals("Release x.y.z.Final", result.get(1));
    }


    @Test
    @Order(4)
    @DisplayName("Fetching single topic with JPA find method")
    void fetch_single_topic_with_find_method() {

        LOGGER.info("===>>> Test execution ");

        final EntityManager em = provider.em();
        try {

            //  -- Because we are not specifying the subtype (we are only find the root type) the JPA have to consult all tables of all types
            //  select *
            //  from topic t
            //  left outer join Announcement a on a.id = t.id
            //  left outer join Post p on p.id = t.id
            //  where t.id = 123
            final Topic topicAnnouncement = em.find(Topic.class, announcementId);
            final Topic topicPost = em.find(Topic.class, postId);


            Assertions.assertNotNull(topicPost);
            assertTrue(topicPost instanceof Post);
            Assertions.assertNotNull(topicAnnouncement);
            assertTrue(topicAnnouncement instanceof Announcement);


            em.clear();
            // select * from Announcement a inner join topic t on t.id = a.id where a.id=?
            final Announcement announcement = em.find(Announcement.class, announcementId);
            // select * from Post p inner join topic t on t.id = p.id where p.id=?
            final Post post = em.find(Post.class, postId);

            Assertions.assertNotNull(post);
            //noinspection ConstantConditions
            assertTrue(post instanceof Post);
            Assertions.assertNotNull(announcement);
            //noinspection ConstantConditions
            assertTrue(announcement instanceof Announcement);

        } finally {
            em.close();
            LOGGER.info("<<<=== Test execution ");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Fetching with JPQL Aggregator Entity (Dashboard) fetching all associates subtypes (Post, Announcement)  implementations instances")
    void fetching_dashboard_with_topics_eagerly_with_JPQL() {

        LOGGER.info("===>>> Test execution ");

        // select *
        // from dashboard d
        //   left outer join topic t on t.dashboard_id = d.id
        //   left outer join Announcement a on t.id = a.id
        //   left outer join Post p on t.id = p.id
        // where d.id = 123

        final Dashboard dashboard = provider.doItWithReturn(em ->

            em.createQuery("""
                    select distinct d from Dashboard d left join fetch d.topics where d.id = :__dashboardId
                    """, Dashboard.class)
                .setParameter("__dashboardId", this.dashboardId)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult()

        );

        assertNotNull(dashboard);
        Assertions.assertEquals(dashboardId, dashboard.getId());
        assertNotNull(dashboard.getTopics());
        assertEquals(2, dashboard.getTopics().size());
        final Map<? extends Class<? extends Topic>, List<Topic>> group = dashboard.getTopics().stream().collect(groupingBy(Topic::getClass, Collectors.toList()));
        assertEquals(2, group.size());
        assertEquals(1, group.get(Post.class).size());
        assertEquals(1, group.get(Announcement.class).size());

        LOGGER.info("<<<=== Test execution ");

    }

    @Test
    @Order(5)
    @DisplayName("Fetching associated entity with fetch type")
    void fetch_statistics() {
        LOGGER.info("===>>> Test execution ");


        List<TopicStatistic> topicStatistics = new ArrayList<>();
        // select * from topic_statistics t
        EntityManager em = provider.em();
        try {

            final List<TopicStatistic> statistics = em.createQuery("select s from TopicStatistic s", TopicStatistic.class).getResultList();
            topicStatistics.addAll(statistics);

        } finally {
            em.close();
        }


        //assertEquals(2, topicStatistics.size());
        //noinspection ResultOfMethodCallIgnored
//        final LazyInitializationException lazyInitializationException = assertThrows(LazyInitializationException.class, () ->
//                topicStatistics.stream()
//                        .map(TopicStatistic::getTopic)
//                        .map(Topic::toDescription)
//                        .collect(toUnmodifiableList()));
//        assertTrue(lazyInitializationException.getMessage().matches("could not initialize proxy (.*) no Session"));

        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(6)
    @DisplayName("How to resolve the class name and the discriminator column value in the JPQL query projection")
    public void get_tuple_projection_using_JPQL() {
        LOGGER.info("===>>> Test execution ");

        provider.doIt(em -> {

            // select t.type, count(t.id)
            // from topic t
            //    left outer join Announcement a on t.id=a.id
            //    left outer join Post p on t.id=p.id
            // group by t.type
            // order by t.type
            List<Tuple> resultsWithDiscriminatorColumnValue =
                    em.createQuery(
                            """
                                    select t.class as the_Discriminator_Column_value, count(t) as the_Value
                                    from Topic t
                                    group by t.class
                                    order by t.class
                                    """
                            , Tuple.class
                    ).getResultList();

            // select t.type, count(t.id)
            // from topic t
            //    left outer join Announcement a on t.id=a.id
            //    left outer join Post p on t.id=p.id
            // group by t.type
            // order by t.type
            List<Tuple> resultsWithClassNameValue =
                    em.createQuery(
                            """
                                    select type(t) as the_Class, count(t) as the_Value
                                    from Topic t
                                    group by t.class
                                    order by t.class
                                    """
                            , Tuple.class
                    ).getResultList();

            assertEquals(2, resultsWithDiscriminatorColumnValue.size());
            final List<Object> theValuesAsInteger = resultsWithDiscriminatorColumnValue.stream().map(tuple -> tuple.get("the_Discriminator_Column_value")).collect(toList());
            assertTrue(theValuesAsInteger.stream().allMatch(Integer.class::isInstance));


            assertEquals(2, resultsWithClassNameValue.size());
            final List<Object> theValuesAsClass = resultsWithClassNameValue.stream().map(tuple -> tuple.get("the_Class")).collect(toList());
            assertTrue(theValuesAsClass.stream().allMatch(Class.class::isInstance));

        });

        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(6)
    @DisplayName("How to query by entity type using JPA Criteria API")
    public void query_by_entity_type_using_JPA_Criteria() {

        // NOTE: until now with hibernate 5.6 I can't get root.type() to work on the projection of the query, for example for a group by

        LOGGER.info("===>>> Test execution ");

        //     select
        //        topic0_.title as col_0_0_
        //    from
        //        topic topic0_
        //    left outer join
        //        Announcement topic0_1_
        //            on topic0_.id=topic0_1_.id
        //    left outer join
        //        Post topic0_2_
        //            on topic0_.id=topic0_2_.id
        //    where
        //        topic0_.type=?
        //    order by
        //        topic0_.type asc

        provider.doIt(em -> {

            Class<? extends Topic> sublcass = Post.class;
            final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            final CriteriaQuery<Tuple> query = criteriaBuilder.createQuery(Tuple.class);
            final Root<Topic> root = query.from(Topic.class);

            final List<Tuple> tupleCriteriaQuery =

                    em.createQuery(

                        query.multiselect( root.get(Topic_.title).alias("the_title") )
                        .where(criteriaBuilder.equal(root.type(), sublcass))
                        .orderBy(criteriaBuilder.asc(root.type()))

                    ).getResultList();

            Assertions.assertNotNull(tupleCriteriaQuery);
            Assertions.assertEquals(1, tupleCriteriaQuery.size());
            Assertions.assertEquals("Inheritance with InheritanceType.JOINED", tupleCriteriaQuery.get(0).get("the_title"));

        });

        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(7)
    @DisplayName("How to customize the order by using the type discriminator")
    public void order_the_the_types() {
        LOGGER.info(">>>=== Test execution ");

        provider.doIt(em -> {

            //  select *
            //    from
            //        topic t
            //    left outer join
            //        Announcement a
            //            on t.id=a.id
            //    left outer join
            //        Post p
            //            on t.id=p.id
            //    where
            //        t.dashboard_id=123
            //    order by
            //        case
            //            when t.type=1 then 11
            //            when t.type=0 then 20
            //        end
            final List<Topic> topics = em.createQuery("""
                    select t from Topic t
                    where t.dashboard.id = :__dashboardId
                    order by case when type (t) = Announcement then 11 when type(t) = Post then 20 end 
                    """, Topic.class)
                    .setParameter("__dashboardId", this.dashboardId)
                    .getResultList();


            assertEquals(2, topics.size());
            assertTrue(topics.get(0) instanceof Announcement);
            assertTrue(topics.get(1) instanceof Post);
        });


        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(8)
    @DisplayName("How Select all the Aggregate entities that only associated with a specific single subtype")
    public void select_using_all_jpql_operator() {
        LOGGER.info(">>>=== Test execution ");

        provider.doIt(em -> {

            // -- Select all the Dashboards that only associated with a specify subtype
            //     select
            //        distinct dashboard0_.id as id1_4_,
            //        dashboard0_.name as name2_4_
            //    from
            //        dashboard dashboard0_
            //    where
            //        0=all (
            //            select
            //                topic1_.type
            //            from
            //                topic topic1_
            //            left outer join
            //                Announcement topic1_1_
            //                    on topic1_.id=topic1_1_.id
            //            left outer join
            //                Post topic1_2_
            //                    on topic1_.id=topic1_2_.id
            //            where
            //                topic1_.dashboard_id=dashboard0_.id
            //        )
            //noinspection JpaQlInspection
            final List<Dashboard> dashboardsOnlyWithPostTopics = em.createQuery("""
                    select distinct d 
                    from Dashboard d
                    where Post  = all ( 
                       select type(t) from Topic t where t.dashboard = d
                     )
                    """, Dashboard.class)
                   // .setParameter("__dashboardId", this.dashboardId)
                    .getResultList();


            assertEquals(0, dashboardsOnlyWithPostTopics.size());
        });


        LOGGER.info("<<<=== Test execution ");
    }

    @Test
    @Order(8)
    @DisplayName("How Select all the Aggregate entities that contain any associated with a specific subtype")
    public void select_using_any_jpql_operator() {
        LOGGER.info(">>>=== Test execution ");

        provider.doIt(em -> {

            // -- Select all the Dashboards that only associated with a specify subtype
            //     select
            //        distinct dashboard0_.id as id1_4_,
            //        dashboard0_.name as name2_4_
            //    from
            //        dashboard dashboard0_
            //    where
            //        1=any (
            //            select
            //                topic1_.type
            //            from
            //                topic topic1_
            //            left outer join
            //                Announcement topic1_1_
            //                    on topic1_.id=topic1_1_.id
            //            left outer join
            //                Post topic1_2_
            //                    on topic1_.id=topic1_2_.id
            //            where
            //                topic1_.dashboard_id=dashboard0_.id
            //        )
            //noinspection JpaQlInspection
            final List<Dashboard> dashboardsThatContainsAnyTopicOfTypeAnnouncement = em.createQuery("""
                    select distinct d 
                    from Dashboard d
                    where Announcement = any ( 
                       select type(t) from Topic t where t.dashboard = d
                     )
                    """, Dashboard.class)
                    // .setParameter("__dashboardId", this.dashboardId)
                    .getResultList();


            assertEquals(1, dashboardsThatContainsAnyTopicOfTypeAnnouncement.size());
            assertEquals(DASHBOARD_TITLE_KEY, dashboardsThatContainsAnyTopicOfTypeAnnouncement.get(0).getName());
        });


        LOGGER.info("<<<=== Test execution ");
    }

}
