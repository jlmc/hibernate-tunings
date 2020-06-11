package io.costax.hibernatetunning.customtypes;

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
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from configuration", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from configuration", phase = Sql.Phase.AFTER_TEST_METHOD)
public class StringArrayTest {

    @JpaContext
    JpaProvider provider;

    @Test
    public void persist() {
        provider.doInTx(em -> em.persist(new Configuration("A-0", new String[]{"role1", "role2"})));

        final Configuration configuration = getConfigurationByNaturalId("A-0");

        assertNotNull(configuration);
        assertNotNull(configuration.getRoles());
        assertEquals(2, configuration.getRoles().length);
        assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});
    }

    private Configuration getConfigurationByNaturalId(final String naturalId) {
        return provider.doItWithReturn(em -> em
                .unwrap(Session.class)
                .bySimpleNaturalId(Configuration.class)
                .load(naturalId));
    }

    @Test
    public void persist_null_array() {
        provider.doInTx(em -> em.persist(new Configuration("A-1", null)));

        final Configuration a1 = getConfigurationByNaturalId("A-1");


        assertNotNull(a1);
        assertNull(a1.getRoles());
    }

    @Test
    public void get_by_null_array() {
        provider.doInTx(em -> em.persist(new Configuration("A-9", null)));

        final List<Configuration> configurations = getConfigurationsByRolesArray(null);

        assertNotNull(configurations);
        assertEquals(configurations.size(), 1);
        assertEquals("A-9", configurations.get(0).getTenant());
        assertNull(configurations.get(0).getRoles());
    }

    @Test
    public void persist_empty_array() {
        provider.doInTx(em -> em.persist(new Configuration("A-3", new String[]{})));

        final Configuration a1 = getConfigurationByNaturalId("A-3");

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(0, a1.getRoles().length);
    }

    @Test
    public void update_existing_array() {
        final String tenant = "A-4";

        provider.doInTx(em -> em.persist(new Configuration(tenant, new String[]{"A1"})));

        provider.doInTx(em -> {

            final Configuration configuration = em.unwrap(Session.class).bySimpleNaturalId(Configuration.class).load(tenant);

            final String[] roles = new String[]{"B1", "C1", "D1"};

            configuration.setRoles(roles);
        });


        final Configuration a1 = getConfigurationByNaturalId(tenant);

        assertNotNull(a1);
        assertNotNull(a1.getRoles());
        assertEquals(3, a1.getRoles().length);
        assertArrayEquals(new String[]{"B1", "C1", "D1"}, a1.getRoles());
    }

    @Test
    public void equals_array_in_query() {
        final String tenant = "A-5";
        provider.doInTx(em -> {
            em.persist(new Configuration(tenant + "-1", new String[]{"A1", "B1"}));
            em.persist(new Configuration(tenant + "-2", new String[]{"A1", "B1", "C2"}));
        });

        final List<Configuration> configurations1 = getConfigurationsByRolesArray(new String[]{"A1", "B1"});

        assertEquals(1, configurations1.size());
        assertEquals("A-5-1", configurations1.get(0).getTenant());

        final List<Configuration> configurations2 = getConfigurationsByRolesArray(new String[] {"B1", "A1"});

        assertEquals(0, configurations2.size());
    }

    private List<Configuration> getConfigurationsByRolesArray(final String[] roles) {
        if (roles == null) {
            return provider.doItWithReturn(em -> em
                    .createQuery("select c from Configuration c where c.roles is null", Configuration.class)
                    .getResultList());
        } else {
            return provider.doItWithReturn(em -> em
                    .createQuery("""
                            select c 
                            from Configuration c 
                            where c.roles = :roles
                            """, Configuration.class)
                            .setParameter("roles", roles)
                            .getResultList());
        }
    }
}
