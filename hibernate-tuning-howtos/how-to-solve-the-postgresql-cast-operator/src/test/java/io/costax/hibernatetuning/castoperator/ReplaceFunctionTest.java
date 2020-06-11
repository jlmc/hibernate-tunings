package io.costax.hibernatetuning.castoperator;

import io.costax.hibernatetunig.model.Project;
import io.costax.hibernatetunig.model.Project_;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.IntStream;

/**
 * If we need to use a <b>DB proprietary functions</b> or even <b>user functions</b> in the query projections (between SELECT and FROM keywords)
 * then those functions must be stored in the dialect that we intend to use.
 *
 * <p>
 * In this example we are registering them in classes: {@link io.costax.hibernatetunig.customdialects.CustomPostgreSqlDialect}
 * </p>
 *
 * <p>
 * Please checkout and see how we can register a functions.
 * </p>
 *
 * @see io.costax.hibernatetunig.customdialects.CustomPostgreSqlDialect
 */
@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(statements = {
        "delete from Issue where true",
        "delete from Project where true"
}, phase = Sql.Phase.AFTER_TEST_METHOD)
public class ReplaceFunctionTest {

    @JpaContext
    public JpaProvider provider;

    @PersistenceContext
    EntityManager em;


    @BeforeEach
    public void populate() {
        provider.doInTx(em -> {

        em.createQuery("delete from Issue ").executeUpdate();
        em.createQuery("delete from Project ").executeUpdate();

        IntStream.rangeClosed(1, 5)
                .unordered()
                .mapToObj(i -> Project.of(String.format("U%d", i)))
                .forEach(em::persist);
        });
    }

    @Test
    @Order(0)
    public void using_replace_with_jpql() {
        /*
         *  If we need to use proprietary functions or even user functions in the query projections (between SELECT and FROM keywords)
         *  then those functions must be registered in the dialect that we intend to use.
         *
         *  In this example we are registering them in the class: CustomPostgreSqlDialect
         *
         *  Please checkout and see how we can register a function.
         */

        //  select replace( trim(p.title), 'U', '') from project p  where cast( replace( trim(p.title), 'U', '') as int4) > 3 order by replace(p.title, 'U', '')
        final List<String> projectNamesWithoutSuffix = em.createQuery(
                """
                select function('replace', trim( p.title ),  'U', '')
                from Project p 
                where cast( function('replace', trim( p.title ),  'U', '') as int ) > :shortLimit 
                order by function('replace', p.title,  'U', '') 
                        """, String.class)
                .setParameter("shortLimit", 3)
                .getResultList();

        Assertions.assertEquals(2, projectNamesWithoutSuffix.size());
        Assertions.assertTrue(projectNamesWithoutSuffix.containsAll(List.of("4", "5")));

    }

    @Test
    @Order(1)
    public void using_replace_with_criteria() {

        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<String> cq = cb.createQuery(String.class);
        final Root<Project> from = cq.from(Project.class);

        final Expression<String> upperU = cb.literal("U");
        final Expression<String> empty = cb.literal("");
        final Expression<Integer> shortLimit = cb.parameter(Integer.class, "_shortLimit");

        //@formatter:off

        cq.select(
                cb.function("replace", String.class, cb.trim(from.get(Project_.title)), upperU, empty)
            )
            .where(
                    cb.greaterThan(
                            cb.function("replace", String.class, cb.trim(from.get(Project_.title)), upperU, empty).as(Integer.class)
                             , shortLimit)
            )
            .orderBy(
                    cb.asc(cb.function("replace", String.class, cb.trim(from.get(Project_.title)), upperU, empty)));

        final List<String> projectNamesWithoutSuffix = em.createQuery(cq)
                .setParameter("_shortLimit", 3)
                .getResultList();

        //@formatter:on

        Assertions.assertEquals(2, projectNamesWithoutSuffix.size());
        Assertions.assertTrue(projectNamesWithoutSuffix.containsAll(List.of("4", "5")));
    }
}
