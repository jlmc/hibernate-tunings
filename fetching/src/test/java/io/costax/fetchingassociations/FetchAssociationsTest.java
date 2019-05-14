package io.costax.fetchingassociations;

import io.costax.model.*;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.hibernate.jpa.QueryHints;
import org.hibernate.query.NativeQuery;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

public class FetchAssociationsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchAssociationsTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void fetch_using_find_method() {
        /**
         * By Default if the project is not marked is Lazy loaded The project reference is
         * loaded eagle using a left join.
         * In the current example the relationship is marked with Lazy loading so two select will be performed,
         * but only when the data is needed.
         */
        final Issue issue = provider.em().find(Issue.class, 1L);

        LOGGER.info("Issue : [{}]", issue);

        final Project project = issue.getProject();

        LOGGER.info("Project : [{}]", project);
    }

    @Test
    public void fetch_using_jpql_query() {

        /**
         * Hibernate generates two queries: one for loading the PostComment entity and another one for
         * initializing the post association.
         *
         * Every time an entity is fetched via an entity query (JPQL or Criteria API) without
         * explicitly fetching all the FetchType.EAGER associations, Hibernate generates additional
         * SQL queries to initialize those relationships as well.
         */
        provider.em().createQuery("select i from Issue i where id = :_id", Issue.class)
                .setParameter("_id", 1L)
                .getSingleResult();
    }

    @Test
    public void fetch_using_entity_graph() {
        final EntityManager entityManager = provider.em();

        EntityGraph<Issue> postEntityGraph = entityManager.createEntityGraph(Issue.class);
        postEntityGraph.addAttributeNodes("project");

        Issue comment = entityManager.find(Issue.class, 1L,
                Collections.singletonMap("javax.persistence.fetchgraph", postEntityGraph)
        );
    }


    /**
     * For @OneToMany and @ManyToMany associations, Hibernate uses its own collection Proxy
     * implementations (e.g. PersistentBag, PersistentList, PersistentSet, PersistentMap) which
     * can execute the lazy loading SQL statement on demand.
     */

    @Test
    public void fetch_one_to_many_without_fetch() {

        exception.expect(LazyInitializationException.class);

        final EntityManager em = provider.createdEntityManagerUnRuled();
        final Project project = em.createQuery(
                "select distinct p " +
                        "from Project p " +
                        "inner join p.issues " +
                        "where p.id = :_id", Project.class)
                .setParameter("_id", 1L)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult();

        LOGGER.info("Project: [{}]", project);
        Assert.assertThat(project, Matchers.notNullValue());

        em.close();

        project.getIssues().forEach(issue ->

                LOGGER.info("Issue: [{}]", issue)

        );
    }

    /**
     * The best way yo deal with the LazyInitializationException is to fetch all the required associations as long as the Persistence Context is open. Using the fetch JPQL directive, a
     * custom entity graph, or the initialize method of the org.hibernate.Hibernate utility, the lazy
     */


    @Test
    public void fetch_one_to_many_without_fetch_dealing_with_LazyInitializationException() {

        //exception.expect(LazyInitializationException.class);

        final EntityManager em = provider.createdEntityManagerUnRuled();
        final Project project = em.createQuery(
                "select distinct p " +
                        "from Project p " +
                        "inner join p.issues " +
                        "where p.id = :_id", Project.class)
                .setParameter("_id", 1L)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult();

        LOGGER.info("Project: [{}]", project);
        Assert.assertThat(project, Matchers.notNullValue());

        //Hibernate.unproxy(project.getIssues());
        Hibernate.initialize(project.getIssues());

        em.close();

        project.getIssues().forEach(issue ->

                LOGGER.info("Issue: [{}]", issue)

        );
    }

    @Test
    public void fetch_one_to_many_with_fetch() {

        final EntityManager em = provider.createdEntityManagerUnRuled();
        final Project project = em.createQuery(
                "select distinct p " +
                        "from Project p " +
                        "inner join fetch p.issues " +
                        "where p.id = :_id", Project.class)
                .setParameter("_id", 1L)
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false)
                .getSingleResult();

        LOGGER.info("Project: [{}]", project);
        Assert.assertThat(project, Matchers.notNullValue());

        em.close();

        project.getIssues().forEach(issue ->

                LOGGER.info("Issue: [{}]", issue)

        );

    }

    @Test
    public void fetch_using_result_transformer_to_dto() {
        final Long issueId = 1L;

        final EntityManager em = provider.em();

        List<IssueNodeTree> IssuesNodeTree =

                em.createNativeQuery(
                        "with recursive issues_tree as ( " +
                                "    select i.id, i.title, i.parent_id " +
                                "    from issue i " +
                                "    where i.parent_id isnull " +
                                "    and i.id = :_issueId " +
                                "    union all " +
                                "    select ii.id, ii.title, ii.parent_id " +
                                "    from issue ii " +
                                "    inner join issues_tree on issues_tree.id = ii.parent_id " +
                                ") select id, title, parent_id from issues_tree"
                        , "IssueNodeTreeMapper")
                        .unwrap(NativeQuery.class)
                        .setParameter("_issueId", issueId)
                        .setResultTransformer(new IssueNodeTreeResultTransformer())
                        .getResultList();
    }

    @Test
    public void fetch_using_result_transformer_to_entity() {
        final Long issueId = 1L;

        final EntityManager em = provider.em();

        List<Issue> issues =
                em.createNativeQuery(
                        "with recursive issues_tree as ( " +
                                "   select i.id, i.version, i.project_id, i.title, i.description, i.create_at, i.parent_id " +
                                "   from issue i " +
                                "   where i.parent_id isnull " +
                                "   and i.id = :_issueId " +
                                "   union all " +
                                "   select ii.id, ii.version, ii.project_id, ii.title, ii.description, ii.create_at, ii.parent_id " +
                                "   from issue ii " +
                                "   inner join issues_tree on issues_tree.id = ii.parent_id " +
                                ") select id, " +
                                "   version, " +
                                "   project_id, " +
                                "   title, description, " +
                                "   create_at, " +
                                "   COALESCE( parent_id, 0) as parentId from issues_tree "
                        , "IssueTreeMapping")
                        .unwrap(NativeQuery.class)
                        .setHint(QueryHints.HINT_READONLY, true)
                        .setParameter("_issueId", issueId)
                        .setResultTransformer(new IssueTreeResultTransformer(em))
                        .getResultList();

        Assert.assertThat(issues, Matchers.hasSize(1));

    }
}
