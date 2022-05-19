package io.costax.hibernatetunning.paginantion.hibernate6;

import io.costax.hibernatetunning.paginantion.data.DataPopulate;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * The following list shows the 3 most commonly used ResultTransformers in Hibernate 4 and 5. These are still available in Hibernate 6 and now implement the TupleTransformer and/or ResultListTransformer interfaces.
 * <p>
 * - AliasToBeanResultTransformer – Instantiates and sets attributes on DTO objects based on the alias defined in the query.
 * - ToListResultTransformer– Maps each record in the query result to a java.util.List.
 * - AliasToEntityMapResultTransformer – Maps the aliased values of each record in the query result to a java.util.Map.
 * You can also implement your own transformation:
 * <p>
 * In Hibernate 4 and 5, you need to implement the ResultTransformer interface and handle the mapping of each result set record in the transformTuple method.
 * In Hibernate 6, you need to implement the functional interfaces TupleTransformer or ResultListTransformer.
 */

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HibernateResultTransformerH6Test {

    @JpaContext
    public JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @BeforeEach
    public void populate() {
        DataPopulate.executePopulate(provider.em());
    }

    @AfterEach
    public void clean() {
        DataPopulate.executeCleanUp(provider.em());
    }

    /**
     * AliasToBeanResultTransformer
     * <p>
     * Hibernate’s AliasToBeanResultTransformer provides another way based on the bean specification.
     * It uses the default constructor of the DTO class to instantiate a new object.
     * <p>
     * In the next step, Hibernate uses reflection to call a setter method for each aliased value in the query.
     * <p>
     * That makes it a great fit for DTOs that are implemented as a standard Java class but not as a Java record.
     */
    @Test
    @SuppressWarnings("CommentedOutCode")
    @DisplayName("Using Hibernate 6 AliasToBeanResultTransformer")
    public void usingAliasToBeanResultTransformer() {
        // Prior to Hibernate 6
        /*
        Query query =
                em.unwrap(Session.class).createQuery(
                        """
                                select d.id as id, d.title as title, count(pl.id) as total
                                from tasks.todo d
                                  left join tasks.todo_comment pl on pl.todo_id = d.id
                                  group by d.id, d.title order by d.id
                                """)
                       .setFirstResult(0)
                       .setMaxResults(10)
                       .setResultTransformer(new AliasToBeanResultTransformer(MySummaryUsingGetterAndSetters.class));
         */

        // With Hibernate 6
        final Session session = em.unwrap(Session.class);
        Query query =
                session.createNativeQuery(
                               """
                                       select d.id as id, d.title as title, count(pl.id) as total
                                       from tasks.todo d 
                                         left join tasks.todo_comment pl on pl.todo_id = d.id
                                         group by d.id, d.title order by d.id
                                       """)
                       .setFirstResult(0)
                       .setMaxResults(10)
                       //.addScalar("id", LongJavaType.class)
                       //.addScalar("title", String.class)
                       //.addScalar("total", BigInteger.class)
                       .setTupleTransformer(
                               new AliasToBeanResultTransformer<MySummaryUsingGetterAndSetters>(
                                       MySummaryUsingGetterAndSetters.class
                               )
                       );

        List<MySummaryUsingGetterAndSetters> list = query.list();
        assertResult(list);
    }

    /**
     * And as you can see in the following code snippet,
     * the implementation of a custom transformer in Hibernate 6 is much more concise.
     */
    @Test
    @DisplayName("Using Hibernate 6 AliasToBeanResultTransformer more concise")
    public void usingAliasToBeanResultTransformerMoreConcise() {
        Session session = em.unwrap(Session.class);
        Query query =
                session.createNativeQuery(
                               """
                                       select d.id as id, d.title as title, count(pl.id) as total
                                       from tasks.todo d 
                                         left join tasks.todo_comment pl on pl.todo_id = d.id
                                         group by d.id, d.title order by d.id
                                       """)
                       .setFirstResult(0)
                       .setMaxResults(10)
                       //.addScalar("id", LongJavaType.class)
                       //.addScalar("title", String.class)
                       //.addScalar("total", BigInteger.class)
                       .setTupleTransformer(((tuples, aliases) -> {
                           System.out.println("-- Transform tuple -- ");
                           MySummaryUsingGetterAndSetters entry = new MySummaryUsingGetterAndSetters();

                           entry.setId((Long) tuples[0]);
                           entry.setTitle((String) tuples[1]);
                           entry.setTotal((Long) tuples[2]);

                           return entry;
                       }));

        List<MySummaryUsingGetterAndSetters> list = query.list();
        System.out.println();
    }

    /**
     * And as you can see in the following code snippet,
     * the implementation of a custom transformer in Hibernate 6 is much more concise.
     */
    @Test
    @DisplayName("Using Hibernate 6 AliasToBeanResultTransformer To Java Record")
    public void usingAliasToBeanResultTransformerToJavaRecord() {
        Session session = em.unwrap(Session.class);
        Query query =
                session.createNativeQuery(
                               """
                                       select d.id as id, d.title as title, count(pl.id) as total
                                       from tasks.todo d 
                                         left join tasks.todo_comment pl on pl.todo_id = d.id
                                         group by d.id, d.title order by d.id
                                       """)
                       .setFirstResult(0)
                       .setMaxResults(10)
                       .setTupleTransformer(((tuples, aliases) -> {
                           System.out.println("-- Transform tuple -- ");
                           return new Report((Long) tuples[0], (String) tuples[1], (Long) tuples[2]);
                       }));

        List list = query.list();
        System.out.println(list);
    }

    @Test
    @DisplayName("Using Hibernate 6 AliasToBeanConstructorResultTransformer more concise")
    public void usingAliasToBeanConstructorResultTransformer() throws NoSuchMethodException {
        final Session session = provider.em().unwrap(Session.class);

        Constructor<MySummaryWithConstructor> constructor = MySummaryWithConstructor.class.getDeclaredConstructor(
                Long.class,
                String.class,
                Long.class);

        List<MySummaryWithConstructor> todos = session.createNativeQuery(
                                                              """
                                                                      select d.id as id, d.title as title, count(pl.id) as total
                                                                      from tasks.todo d
                                                                        left join tasks.todo_comment pl on pl.todo_id = d.id
                                                                      group by d.id, d.title order by d.id
                                                                      """)
                                                      .setFirstResult(0)
                                                      .setMaxResults(10)
                                                      .setTupleTransformer(new AliasToBeanConstructorResultTransformer(constructor))
                                                      .list();

        assertResult2(todos);
    }

    public static class MySummaryWithConstructor {
        private final Long id;
        private final String title;
        private final Long total;

        public MySummaryWithConstructor(final Long id,
                                        final String title,
                                        final Long total) {
            this.id = id;
            this.title = title;
            this.total = total;
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Long getTotal() {
            return total;
        }
    }

    public static class MySummaryUsingGetterAndSetters {
        private Long id;
        private String title;
        private Long total;

        public MySummaryUsingGetterAndSetters() {
        }

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(final Long total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "MySummaryUsingGetterAndSetters{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    ", total=" + total +
                    '}';
        }
    }

    private void assertResult(List<MySummaryUsingGetterAndSetters> list) {
        assertEquals(10, list.size());

        assertEquals(1L, list.get(0).getId());
        assertEquals("todo-1", list.get(0).getTitle());
        assertEquals(3L, list.get(0).getTotal());

        assertEquals("todo-10", list.get(9).getTitle());
    }

    private void assertResult2(List<MySummaryWithConstructor> list) {
        assertEquals(10, list.size());

        assertEquals(1L, list.get(0).getId());
        assertEquals("todo-1", list.get(0).getTitle());
        assertEquals(3L, list.get(0).getTotal());

        assertEquals("todo-10", list.get(9).getTitle());
    }
}

record Report(Long id, String title, Long total) {
}
