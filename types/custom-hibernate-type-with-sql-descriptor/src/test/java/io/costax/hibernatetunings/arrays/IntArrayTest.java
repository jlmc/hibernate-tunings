package io.costax.hibernatetunings.arrays;

import io.costax.hibernatetunings.entities.Configuration;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IntArrayTest {

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
            final Configuration configuration = new Configuration(
                    "A-0",
                    new String[]{"role1", "role2"},
                    new int[]{1, 2});
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration configuration = unwrap.bySimpleNaturalId(Configuration.class).load("A-0");

        Assert.assertNotNull(configuration);
        Assert.assertNotNull(configuration.getRoles());
        Assert.assertEquals(2, configuration.getRoles().length);
        //Assert.assertTrue(Arrays.equals(a1.getRoles(), new String[]{"role1", "role2"}));
        Assert.assertArrayEquals(configuration.getRoles(), new String[]{"role1", "role2"});

        Assert.assertNotNull(configuration.getRoles());
        Assert.assertEquals(2, configuration.getNumbers().length);
        Assert.assertArrayEquals(configuration.getNumbers(), new int[]{1, 2});
    }

    @Test
    public void t01_should_persist_null_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-1", null, null);
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-1");

        Assert.assertNotNull(a1);
        Assert.assertNull(a1.getRoles());
        Assert.assertNull(a1.getNumbers());
    }

    @Test
    public void t02_should_persist_empty_array() {
        provider.doInTx(em -> {
            final Configuration configuration = new Configuration("A-3", new String[]{}, new int[]{});
            em.persist(configuration);
        });

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load("A-3");

        Assert.assertNotNull(a1);
        Assert.assertNotNull(a1.getRoles());
        Assert.assertEquals(0, a1.getRoles().length);
        Assert.assertEquals(0, a1.getNumbers().length);
    }

    @Test
    public void t03_should_update_existing_array() {
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

        final Session unwrap = provider.em().unwrap(Session.class);
        final Configuration a1 = unwrap.bySimpleNaturalId(Configuration.class).load(tenant);

        Assert.assertNotNull(a1);
        Assert.assertNotNull(a1.getRoles());
        Assert.assertEquals(3, a1.getRoles().length);
        Assert.assertArrayEquals(new String[]{"B1", "C1", "D1"}, a1.getRoles());
        Assert.assertArrayEquals(new int[]{0, 2, 4, 8, 16, 32, 64, 128, 254}, a1.getNumbers());
    }

}
