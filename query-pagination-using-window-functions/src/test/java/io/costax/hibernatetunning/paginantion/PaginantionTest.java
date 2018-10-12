package io.costax.hibernatetunning.paginantion;

import io.costax.rules.EntityManagerProvider;
import org.hibernate.query.NativeQuery;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class PaginantionTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void createRecords() {

        provider.beginTransaction();
        EntityManager em = provider.em();

        ProgrammingLanguage java = ProgrammingLanguage.of("Java");
        ProgrammingLanguage javaScript = ProgrammingLanguage.of("javaScript");
        ProgrammingLanguage sql = ProgrammingLanguage.of("sql");
        ProgrammingLanguage python = ProgrammingLanguage.of("python");
        ProgrammingLanguage scala = ProgrammingLanguage.of("scala");
        ProgrammingLanguage c = ProgrammingLanguage.of("c");
        ProgrammingLanguage cplusplus = ProgrammingLanguage.of("cplusplus");
        ProgrammingLanguage html = ProgrammingLanguage.of("html");
        ProgrammingLanguage lua = ProgrammingLanguage.of("lua");
        ProgrammingLanguage vb = ProgrammingLanguage.of("vb");
        ProgrammingLanguage matlab = ProgrammingLanguage.of("matlab");
        ProgrammingLanguage php = ProgrammingLanguage.of("PHP");
        ProgrammingLanguage rubyOnRails = ProgrammingLanguage.of("Ruby on Rails");
        ProgrammingLanguage iosSwift = ProgrammingLanguage.of("iOS/Swift");

        em.persist(java);
        em.persist(javaScript);
        em.persist(sql);
        em.persist(python);
        em.persist(scala);
        em.persist(c);
        em.persist(cplusplus);
        em.persist(html);
        em.persist(lua);
        em.persist(vb);
        em.persist(matlab);
        em.persist(php);
        em.persist(rubyOnRails);
        em.persist(iosSwift);

        List<ProgrammingLanguage> programmingLanguages = Arrays.asList(
                java,
                javaScript,
                sql,
                python,
                scala,
                c,
                cplusplus,
                html,
                lua,
                vb,
                matlab,
                php,
                rubyOnRails,
                iosSwift);

        int size = 1;
        for (int i = 0; i < 60; i++) {
            Developer d = Developer.of(String.format("Developer-%d", i), String.format("dukes-%d", i));

            for (int p = 0; p < size; p++) {
                d.add(programmingLanguages.get(p));
            }

            if (i % 10 == 0) {
                size++;
            }

            em.persist(d);
        }

        provider.commitTransaction();
    }

    @Test
    public void shouldSetMaxResultsJPQL() {
        List<Developer> developers = provider.em().createQuery("select d from Developer d order by d.id", Developer.class)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("Developer-0", developers.get(0).getName());
        assertEquals("Developer-9", developers.get(9).getName());
    }

    @Test
    public void shouldUseSetFirstResultAndSetMaxResultsJPQL() {
        List<Developer> developers = provider.em().createQuery("select d from Developer d order by d.id", Developer.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("Developer-10", developers.get(0).getName());
        assertEquals("Developer-19", developers.get(9).getName());
    }

    @Test
    public void shouldUseSetFirstResultAndSetMaxResultsJPQLToDTOProjection() {
        List<DeveloperSummary> developers = provider.em()
                .createQuery("select " +
                        "new io.costax.hibernatetunning.paginantion.DeveloperSummary(" +
                        "   d.id, d.name, count (pl)" +
                        ") " +
                        "from Developer d join d.programmingLanguages pl " +
                        "group by d.id, d.name order by d.id", DeveloperSummary.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("Developer-10", developers.get(0).getName());
        assertThat(developers.get(0).getNumOfProgramingLanguages(), is(2));
        assertEquals("Developer-19", developers.get(9).getName());
        assertThat(developers.get(9).getNumOfProgramingLanguages(), is(3));
    }

    @Test
    public void usingNativeQuery() {

        List<Tuple> developers = provider.em()
                .createNativeQuery(
                        "select d.* from developer d order by d.id", Tuple.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

        // Note That we can use also the Entity unsted of a Tuple

        assertEquals(10, developers.size());
        assertEquals("Developer-10", developers.get(0).get("name", String.class));
        assertEquals("Developer-19", developers.get(9).get("name", String.class));
    }

    /**
     * Hibernate will issue the following warning message:
     * <p>
     * HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!
     * <p>
     * This is because Hibernate wants to fetch entities fully along with their collections as
     * indicated by the JOIN FETCH clause while the SQL-level pagination could truncate the ResultSet
     * possibly leaving a parent Developer entity with fewer elements in the comments collection.
     * <p>
     * The problem with the HHH000104 warning is that Hibernate will fetch the product of
     * Developer and ProgramingLanguage entities, and due to the result set size,
     * the query response time is going to be significant.
     */
    @Test
    public void shouldPaginateInMemoryWhenWeUseJoinFetch() {
        List<Developer> developers = provider.em()
                .createQuery("select distinct d from Developer d left join fetch d.programmingLanguages order by d.id", Developer.class)
                .setFirstResult(10)
                .setMaxResults(10)
                .getResultList();

        assertEquals(10, developers.size());
        assertEquals("Developer-10", developers.get(0).getName());
        assertEquals("Developer-19", developers.get(9).getName());
    }

    /**
     * In order to work around the previous limitation (HHH000104), we have to use a Window Function query:
     */
    @Test
    public void paginationUsingNativeQueryWithWindowFunction() {

        final int pageSize = 10;
        final int page = 1;
        final int firstRecord = page * pageSize;
        int lastRecord = firstRecord + pageSize;

        List resultList = provider.em()
                .createNativeQuery("select p_pc_r.* " +
                        "from ( " +
                        "        select d_pls.*, dense_rank() OVER (ORDER BY developer_id) rank " +
                        "        from ( " +
                        "                select d.*, l.* as _lName, dpl.developer_id " +
                        "                from developer d " +
                        "                left join developer_programmig_language dpl on d.id = dpl.developer_id " +
                        "                left join programming_language l on dpl.tag_id = l.id " +
                        "                order by d.id " +
                        "        ) d_pls " +
                        ") p_pc_r " +
                        "where p_pc_r.rank > :firstRecord " +
                        "and p_pc_r.rank <= :lastRecord ")
                .setParameter("firstRecord", firstRecord)
                .setParameter("lastRecord", lastRecord)
                .unwrap(NativeQuery.class)
                .addEntity("d", Developer.class)
                .addEntity("l", ProgrammingLanguage.class)
                .setResultTransformer(DistinctDeveloperResultTransformer.INSTANCE)
                .getResultList();

        List<Developer> developers = resultList;
        assertEquals(10, developers.size());
        assertEquals("Developer-10", developers.get(0).getName());
        assertEquals("Developer-19", developers.get(9).getName());
    }

}

