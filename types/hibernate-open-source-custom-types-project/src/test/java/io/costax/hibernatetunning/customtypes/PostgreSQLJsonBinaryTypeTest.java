package io.costax.hibernatetunning.customtypes;

import io.costa.hibernatetunings.entities.Developer;
import io.costa.hibernatetunings.entities.Event;
import io.costa.hibernatetunings.entities.Location;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;

public class PostgreSQLJsonBinaryTypeTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void savejsonB() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

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

        provider.commitTransaction();
    }

    @Test
    public void saveArrays() {
        final EntityManager em = provider.em();
        provider.beginTransaction();
        Event devox = Event.of("Web Submit", Location.of("Portugal", "Lisbon"));

        devox.setPortsName(new String[]{"Paris", "Belgian"});
        devox.setPriceBaseValues(new int[]{76, 5});

        em.persist(devox);
        provider.commitTransaction();
    }
}
