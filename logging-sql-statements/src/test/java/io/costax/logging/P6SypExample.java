package io.costax.logging;

import io.costax.hibernatetuning.p6syp.entities.HumanResource;
import org.hibernate.cfg.Environment;
import org.junit.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P6SypExample {

    private static EntityManagerFactory EMF;
    private EntityManager em;

    // @Rule
    // public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");
    @BeforeClass
    public static void initEntityManagerFactory() {
        final Map<String, String> settings = new HashMap<>();

        settings.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        //settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/postgresdemos");
        settings.put(Environment.URL, "jdbc:p6spy:postgresql://localhost:5432/postgresdemos");
        settings.put(Environment.USER, "postgres");
        settings.put(Environment.PASS, "postgres");
        //settings.put(Environment.HBM2DDL_AUTO, "validate");

        EMF = Persistence.createEntityManagerFactory("it", settings);
    }

    @AfterClass
    public static void shutdown() {
        EMF.close();
    }

    @Before
    public void openEm() {
        this.em = EMF.createEntityManager();
    }

    @After
    public void closeEm() {
        this.em.clear();
        this.em.close();
    }


    @Test
    public void shouldLogInsertStatement() {


        List<HumanResource> rhs = em
                .createQuery("select hr from HumanResource hr", HumanResource.class)
                .getResultList();

        rhs.stream().forEach(System.out::println);
    }

    @Test
    public void shouldInser10NewsRh() {
        em.getTransaction().begin();

        for (int i = 10; i < 20; i++) {
            em.persist(HumanResource.of("demo-abc-" + i, "ABC " + i));
        }

        em.flush();

        em.getTransaction().rollback();
    }
}
