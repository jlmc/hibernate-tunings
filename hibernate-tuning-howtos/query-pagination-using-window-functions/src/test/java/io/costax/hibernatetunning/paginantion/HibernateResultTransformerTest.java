package io.costax.hibernatetunning.paginantion;

import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.junit.*;
import org.junit.runners.MethodSorters;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HibernateResultTransformerTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    public void populate() {
        DataPopulator.with(provider).populate();
    }

    @After
    public void clean() {
        DataPopulator.with(provider).clean();
    }

    @Test
    public void usingAliasToBeanResultTransformer() {
        final Session session = provider.em().unwrap(Session.class);

        List<MySummaryUsingGetterAndSetters> todos = session.createSQLQuery(
                "select d.id, d.title, count(pl.id) as total " +
                        "from tasks.todo d " +
                        "left join tasks.todo_comment pl on pl.todo_id = d.id " +
                        "group by d.id, d.title order by d.id")
                .setFirstResult(0)
                .setMaxResults(10)
                .setResultTransformer(new AliasToBeanResultTransformer(MySummaryUsingGetterAndSetters.class))
                .list();


        assertEquals(10, todos.size());
        assertEquals("todo-1", todos.get(0).getTitle());
        assertThat(todos.get(0).getTotal(), is(BigInteger.valueOf(3L)));
        assertEquals("todo-10", todos.get(9).getTitle());
        assertThat(todos.get(9).getTotal(), is(BigInteger.valueOf(0L)));
    }

    @Test
    public void usingAliasToBeanConstructorResultTransformer() throws NoSuchMethodException {
        final Session session = provider.em().unwrap(Session.class);

        Constructor<MySummaryWithConstructor> constructor = MySummaryWithConstructor.class.getDeclaredConstructor(
                BigInteger.class,
                String.class,
                BigInteger.class);

        List<MySummaryWithConstructor> todos = session.createSQLQuery(
                "select d.id, d.title, count(pl.id) as total " +
                        "from tasks.todo d " +
                        "left join tasks.todo_comment pl on pl.todo_id = d.id " +
                        "group by d.id, d.title order by d.id")
                .setFirstResult(0)
                .setMaxResults(10)
                .setResultTransformer(
                        new AliasToBeanConstructorResultTransformer(constructor)
                ).list();

        assertEquals(10, todos.size());
        assertEquals("todo-1", todos.get(0).getTitle());
        assertThat(todos.get(0).getTotal(), is(BigInteger.valueOf(3L)));
        assertEquals("todo-10", todos.get(9).getTitle());
        assertThat(todos.get(9).getTotal(), is(BigInteger.valueOf(0L)));
    }


    public static class MySummaryWithConstructor {
        private final BigInteger id;
        private final String title;
        private final BigInteger total;

        public MySummaryWithConstructor(final BigInteger id, final String title, final BigInteger total) {
            this.id = id;
            this.title = title;
            this.total = total;
        }

        public BigInteger getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public BigInteger getTotal() {
            return total;
        }
    }

    public static class MySummaryUsingGetterAndSetters {
        private BigInteger id;
        private String title;
        private BigInteger total;

        public MySummaryUsingGetterAndSetters() {
        }

        public BigInteger getId() {
            return id;
        }

        public void setId(final BigInteger id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(final String title) {
            this.title = title;
        }

        public BigInteger getTotal() {
            return total;
        }

        public void setTotal(final BigInteger total) {
            this.total = total;
        }
    }
}
