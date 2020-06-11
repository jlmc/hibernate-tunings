package io.costax.hibernatetunings.arrays;

import io.costax.hibernatetunings.entities.Configuration;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from configuration", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from configuration", phase = Sql.Phase.AFTER_TEST_METHOD)
public class IntArrayTest {

    @JpaContext
    public JpaProvider provider;


    private Configuration findConfigurationByNaturalId(final String naturalId) {
        return provider
                .doItWithReturn(em -> em
                        .unwrap(Session.class)
                        .bySimpleNaturalId(Configuration.class)
                        .load(naturalId));
    }

    @Test
    public void should_persist() {
        final String tenant = "A-0";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(
                    tenant,
                    new String[]{"role1", "role2"},
                    new int[]{1, 2});
            em.persist(configuration);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getRoles().length);
        assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});
        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getNumbers().length);
        assertArrayEquals(configuration.getNumbers(), new int[]{1, 2});
    }

    @Test
    public void should_persist_null_array() {
        final String tenant = "A-1";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, null, null);
            em.persist(configuration);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNull(configuration.getRoles());
        assertNull(configuration.getNumbers());
    }

    @Test
    public void should_persist_empty_array() {
        final String tenant = "A-3";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, new String[]{}, new int[]{});
            em.persist(configuration);
        });

        final Configuration a1 = findConfigurationByNaturalId(tenant);

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(0, a1.getRoles().length);
        assertEquals(0, a1.getNumbers().length);
    }

    @Test
    public void should_update_existing_array() {
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

        final Configuration a1 = findConfigurationByNaturalId(tenant);

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(3, a1.getRoles().length);
        assertArrayEquals(new String[]{"B1", "C1", "D1"}, a1.getRoles());
        assertArrayEquals(new int[]{0, 2, 4, 8, 16, 32, 64, 128, 254}, a1.getNumbers());
    }

}
