package io.costax.hibernatetunings.arrays;

import io.costax.hibernatetunings.entities.Configuration;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from configuration", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from configuration", phase = Sql.Phase.AFTER_TEST_METHOD)
public class StringArrayTest {

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
    public void persist() {
        final String tenant = "A-0";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, new String[]{"role1", "role2"});
            em.persist(configuration);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getRoles().length);
        assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});
    }

    @Test
    public void persist_null_array() {
        final String tenant = "A-1";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, null);
            em.persist(configuration);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNull(configuration.getRoles());
    }

    @Test
    public void persist_empty_array() {
        final String tenant = "A-3";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, new String[]{});
            em.persist(configuration);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(0, configuration.getRoles().length);
    }

    @Test
    public void update_existing_array() {
        final String tenant = "A-4";

        provider.doInTx(em -> {
            final Configuration configuration = new Configuration(tenant, new String[]{"A1"});
            em.persist(configuration);
        });

        provider.doInTx(em -> {
            final Configuration configuration = em.unwrap(Session.class).bySimpleNaturalId(Configuration.class).load(tenant);
            final String[] roles = new String[]{"B1", "C1", "D1"};
            configuration.setRoles(roles);
        });

        final Configuration configuration = findConfigurationByNaturalId(tenant);

        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(3, configuration.getRoles().length);
        assertArrayEquals(new String[]{"B1", "C1", "D1"}, configuration.getRoles());
    }

    @Test
    public void test_equals_array_in_query() {
        final String tenant1 = "tenant-1";
        final String tenant2 = "tenant-2";
        final String tenant3 = "tenant-3";

        provider.doInTx(em -> {
            final Configuration configuration1 = new Configuration(tenant1, new String[]{"A1", "B1"});
            em.persist(configuration1);

            final Configuration configuration2 = new Configuration(tenant2, new String[]{"B1", "A1"});
            em.persist(configuration2);

            final Configuration configuration3 = new Configuration(tenant3, new String[]{"A1", "B1", "C2"});
            em.persist(configuration3);
        });

        final List<Configuration> configurations1 = provider.doItWithReturn(em ->
                em.createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                        .setParameter("roles", new String[]{"A1", "B1"})
                        .getResultList());


        assertEquals(1, configurations1.size());
        assertEquals(tenant1, configurations1.get(0).getTenant());


        final List<Configuration> configurations2 = provider.doItWithReturn(em ->
                em.createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                        .setParameter("roles", new String[]{"B1", "A1"})
                        .getResultList());

        assertEquals(1, configurations2.size());
        assertEquals(tenant2, configurations2.get(0).getTenant());


        final List<Configuration> configurations3 = provider.doItWithReturn(em ->
                em.createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                        .setParameter("roles", new String[]{"A1", "B1", "C2"})
                        .getResultList());
        final List<Configuration> configurations3WithArrayInInverseOrder = provider.doItWithReturn(em ->
                em.createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                        .setParameter("roles", new String[]{"A1", "C2", "B1"})
                        .getResultList());


        assertEquals(1, configurations3.size());
        assertEquals(0, configurations3WithArrayInInverseOrder.size());
        assertEquals(tenant3, configurations3.get(0).getTenant());
        assertArrayEquals(new String[]{"A1", "B1", "C2"}, configurations3.get(0).getRoles());
    }
}
