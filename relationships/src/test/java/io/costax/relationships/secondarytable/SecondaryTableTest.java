package io.costax.relationships.secondarytable;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecondaryTableTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void test() {
        provider.doInTx(em -> {

            Zone zoneGreen = new Zone(1, "Zone Green", "GREEN");
            Zone zoneRed = new Zone(2, "Zone Red", "RED");

            em.persist(zoneGreen);
            em.persist(zoneRed);
        });

        /*
         * select
         *   zone0_.id as id1_18_0_,
         *   zone0_.name as name2_18_0_,
         *   zone0_.version as version3_18_0_,
         *   zone0_1_.pseudonym as pseudony1_19_0_
         * from
         * zone zone0_
         *   left outer join zone_details zone0_1_ on zone0_.id=zone0_1_.id
         * where zone0_.id=?
         */
        //final Zone zone = provider.em().find(Zone.class, 1);

        provider.doInTx(em -> {

            final Zone zone = em.find(Zone.class, 1);

           // em.remove(zone);

            zone.setPseudonym("Abcd");

        });

    }
}
