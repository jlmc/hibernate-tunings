package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.entities.Developer;
import io.costax.hibernatetunings.entities.Event;
import io.costax.hibernatetunings.entities.Location;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = {
        "delete from timesheet where true",
        "delete from event_developer where true",
        "delete from Developer where true",
        "delete from event where true",
}, phase = Sql.Phase.AFTER_TEST_METHOD)
public class PostgresJsonBinaryTypeTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    void save_in_jsonB_column() {
        provider.doInTx(em -> {

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

            em.flush();

        });
    }

    @Test
    void save_array_columns() {
        provider.doInTx(em -> {

            Event devox = Event.of("Web Submit", Location.of("Portugal", "Lisbon"));

            devox.setPortsName(new String[]{"Paris", "Belgian"});
            devox.setPriceBaseValues(new int[]{76, 5});

            em.persist(devox);

        });
    }


}
