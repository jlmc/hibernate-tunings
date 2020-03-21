package io.costax.hibernatetunning;

import io.costa.hibernatetunings.entities.Developer;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.util.List;

/**
 * JEP 355: Text Blocks (Preview)
 * <p>
 * <p>
 * To run this test case we have to enable the jdk 13 preview features. To do that we use the args --enable-preview
 * <p>
 * https://openjdk.java.net/jeps/355
 */
@SuppressWarnings({"unchecked", "SqlDialectInspection"})
public class JDk13TextBlocksTest {
    /*

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void nativeQueryWithTextBlocks() {
        final EntityManager em = provider.em();

        List<Tuple> result = em.createNativeQuery(
                """
                 select d.id, d.name, count(pl.id) over ( partition by d.id order by d.id asc ) as total_languages
                 from developer d
                     left join developer_programing_language dpl on d.id = dpl.developer_id
                     left join programing_language pl on dpl.programing_language_id = pl.id
                 """
                , Tuple.class)
                .getResultList();


        System.out.println(result);
    }

    @Test
    public void queryWithTextBlocks() {

        List<Developer> authors = provider.em()
                .createQuery("""
                                      select a
                                      from Developer a
                                     """,
                        Developer.class)
                .getResultList();
    }
     */
}
