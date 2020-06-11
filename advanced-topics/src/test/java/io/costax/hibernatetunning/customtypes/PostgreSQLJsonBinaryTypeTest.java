package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.entities.Developer;
import io.costax.hibernatetunings.entities.Event;
import io.costax.hibernatetunings.entities.Location;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

@JpaTest(persistenceUnit = "it")
public class PostgreSQLJsonBinaryTypeTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    @DisplayName("save in JsonB column")
    public void saveJsonB() {

        final EntityManager em = provider.em();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        final Developer duke = new Developer.Builder().setNome("duke").createDeveloper();
        final Developer jc = new Developer.Builder().setNome("jlmc").createDeveloper();
        final Developer sadGay = new Developer.Builder().setNome("sad unhappy gay").createDeveloper();
        em.persist(duke);
        em.persist(jc);
        em.persist(sadGay);

        Event webSubmit = Event.of("Web Submit", Location.of("Portugal", "Lisbon"));
        Event oracleCodeOne = Event.of("Oracle code one", Location.of("USA", "S. Francisco"));

        em.persist(webSubmit);
        em.persist(oracleCodeOne);

        webSubmit.registe(jc);
        oracleCodeOne.registe(duke);

        tx.commit();
        em.close();
    }

    @Test
    @DisplayName("save in Array column")
    public void saveArrays() {
        final EntityManager em = provider.em();
        final EntityTransaction tx = em.getTransaction();
        tx.begin();

        Event devox = Event.of("Web Submit", Location.of("Portugal", "Lisbon"));

        devox.setPortsName(new String[]{"Paris", "Belgian"});
        devox.setPriceBaseValues(new int[]{76, 5});

        em.persist(devox);

        tx.commit();
        em.close();
    }
}
