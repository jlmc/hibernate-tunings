package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.entities.Configuration;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StringArrayTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    @After
    public void cleanup() {
        final EntityManager em = provider.createdEntityManagerUnRuled();
        em.getTransaction().begin();

        em.createNativeQuery("delete from configuration").executeUpdate();

        em.getTransaction().commit();
    }

    @Test
    public void t00_should_persist() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-0", new String[]{"role1", "role2"});
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration configuration = unwrap.bySimpleNaturalId(Configuration.class).load("A-0");


        Assert.assertNotNull(configuration);
        Assert.assertNotNull(configuration.getRoles());
        Assert.assertEquals(2, configuration.getRoles().length);
        //Assert.assertTrue(Arrays.equals(a1.getRoles(), new String[]{"role1", "role2"}));
        Assert.assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});
    }

    @Test
    public void t01_should_persist_null_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-1", null);
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-1");

        Assert.assertNotNull(a1);
        Assert.assertNull(a1.getRoles());
    }

    @Test
    public void t02_should_persist_empty_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-3", new String[]{});
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-3");

        Assert.assertNotNull(a1);
        Assert.assertNotNull(a1.getRoles());
        Assert.assertEquals(0, a1.getRoles().length);
    }

    @Test
    public void t03_should_update_existing_array() {
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

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load(tenant);

        Assert.assertNotNull(a1);
        Assert.assertNotNull(a1.getRoles());
        Assert.assertEquals(3, a1.getRoles().length);
        Assert.assertArrayEquals(new String[]{"B1", "C1", "D1"}, a1.getRoles());
    }

    @Test
    public void t04_test_equals_array_in_query() {
        final String tenant = "A-5";
        provider.doInTx(em -> {
            final Configuration configuration1 = new Configuration(tenant + "-1", new String[]{"A1", "B1"});
            em.persist(configuration1);

            final Configuration configuration2 = new Configuration(tenant + "-2", new String[]{"A1", "B1", "C2"});
            em.persist(configuration2);
        });


        final List<Configuration> configurations1 = provider.em().createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                .setParameter("roles", new String[]{"A1", "B1"})
                .getResultList();

        Assert.assertEquals(1, configurations1.size());
        Assert.assertEquals("A-5-1", configurations1.get(0).getTenant());

        final List<Configuration> configurations2 = provider.em().createQuery("select c from Configuration c where c.roles = :roles", Configuration.class)
                .setParameter("roles", new String[]{"B1", "A1"})
                .getResultList();

        Assert.assertEquals(0, configurations2.size());
    }
}
