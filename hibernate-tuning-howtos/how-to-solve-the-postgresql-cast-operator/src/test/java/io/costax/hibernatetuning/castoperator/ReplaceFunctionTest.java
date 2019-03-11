package io.costax.hibernatetuning.castoperator;

import io.costax.hibernatetunig.model.Project;
import io.costax.hibernatetunig.model.Project_;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReplaceFunctionTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @After
    public void cleanup() {
        provider.beginTransaction();
        provider.em().createQuery("delete from Issue ").executeUpdate();
        provider.em().createQuery("delete from Project ").executeUpdate();
        provider.commitTransaction();
    }

    @Before
    public void populate() {
        provider.beginTransaction();

        provider.em().createQuery("delete from Issue ").executeUpdate();
        provider.em().createQuery("delete from Project ").executeUpdate();

        IntStream.rangeClosed(1, 5)
                .unordered()
                .mapToObj(i -> Project.of(String.format("U%d", i)))
                .forEach(provider.em()::persist);

        provider.commitTransaction();
    }

    @Test
    public void t0_using_replace_with_jpql() {
        //@formatter:off

        final EntityManager em = provider.em();

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
                "select function('replace', trim( p.title ),  'U', '')" +
                        "from Project p " +
                        "where cast( function('replace', trim( p.title ),  'U', '') as int ) > :shortLimit " +
                        "order by function('replace', p.title,  'U', '') ", String.class)
                .setParameter("shortLimit", 3)
                .getResultList();

        Assert.assertThat(projectNamesWithoutSuffix, Matchers.hasSize(2));
        Assert.assertThat(projectNamesWithoutSuffix, Matchers.contains("4", "5"));

        //@formatter:on
    }

    @Test
    public void t1_using_replace_with_criteria() {
        final EntityManager em = provider.em();

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

        Assert.assertThat(projectNamesWithoutSuffix, Matchers.hasSize(2));
        Assert.assertThat(projectNamesWithoutSuffix, Matchers.contains("4", "5"));
    }
}
