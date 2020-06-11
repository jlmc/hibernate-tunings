package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.entities.Configuration;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from configuration", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from configuration", phase = Sql.Phase.AFTER_TEST_METHOD)
public class IntArrayTest {

    @JpaContext
    public JpaProvider provider;

    @PersistenceContext
    EntityManager em;


    @Test
    public void persist() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(
                    "A-0",
                    new String[]{"role1", "role2"},
                    new int[]{1, 2});
            em.persist(configuration);
        });

        final Session unwrap = em.unwrap(Session.class);
        final Configuration configuration = unwrap.bySimpleNaturalId(Configuration.class).load("A-0");


        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getRoles().length);
        //assertTrue(Arrays.equals(a1.getRoles(), new String[]{"role1", "role2"}));
        assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});

        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getNumbers().length);
        assertArrayEquals(configuration.getNumbers(), new int[]{1, 2});
    }

    @Test
    public void persist_null_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-1", null, null);
            em.persist(configuration);
        });

        final Session unwrap = em.unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-1");

        assertNotNull(a1);
        assertNull(a1.getRoles());
        assertNull(a1.getNumbers());
    }

    @Test
    public void persist_empty_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-3", new String[]{}, new int[]{});
            em.persist(configuration);
        });

        final Session unwrap = em.unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-3");

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(0, a1.getRoles().length);
        assertEquals(0, a1.getNumbers().length);
    }

    @Test
    public void update_existing_array() {
        final String tenant = "A-4";
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, new String[]{"A1"}, new int[]{1, 2, 3});
            em.persist(configuration);
        });

        provider.doInTx(em -> {

            final Configuration configuration = em.unwrap(Session.class).bySimpleNaturalId(Configuration.class).load(tenant);

            final String[] roles = new String[]{"B1", "C1", "D1"};
            final int[] numbers = new int[]{0, 2, 4, 8, 16, 32, 64, 128, 254};

            configuration.setRoles(roles);
            configuration.setNumbers(numbers);
        });

        final Session unwrap = em.unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load(tenant);

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(3, a1.getRoles().length);
        assertArrayEquals(new String[]{"B1", "C1", "D1"}, a1.getRoles());
        assertArrayEquals(new int[]{0, 2, 4, 8, 16, 32, 64, 128, 254}, a1.getNumbers());
    }

}
