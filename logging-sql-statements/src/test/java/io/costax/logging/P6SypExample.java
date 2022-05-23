package io.costax.logging;

import io.costax.hibernatetuning.p6syp.entities.HumanResource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.Environment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class P6SypExample {

    private static EntityManagerFactory EMF;
    private EntityManager em;

    // @Rule
    // public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");
    @BeforeAll
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

    @AfterAll
    public static void shutdown() {
        EMF.close();
    }

    @BeforeEach
    public void openEm() {
        this.em = EMF.createEntityManager();
    }

    @AfterEach
    public void closeEm() {
        this.em.clear();
        this.em.close();
    }

    @Test
    public void shouldLogInsertStatement() {
        List<HumanResource> rhs = em
                .createQuery("select hr from HumanResource hr", HumanResource.class)
                .getResultList();

        rhs.forEach(System.out::println);
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
