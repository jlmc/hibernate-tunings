package io.costax.models;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TheBestWayToUseJavaRecordWithJPA {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void using_Java_Record_To_Fetch_Data() {

        provider.doInTx(em -> {

            Sport tennis = Sport.of(1, "Tennis");
            Sport futebol = Sport.of(2, "Futebol");
            Sport athletics = Sport.of(3, "Athletics");
            em.persist(tennis);
            em.persist(futebol);
            em.persist(athletics);

            Athlete federer = Athlete.of("Federer", LocalDate.of(1981, Month.AUGUST, 8));
            Athlete usainBolt = Athlete.of("Usain Bolt", LocalDate.of(1986, Month.AUGUST, 21));
            Athlete eusebio = Athlete.of("Eusébio", LocalDate.of(1942, Month.JANUARY, 25));

            federer.addSports(List.of(tennis));
            eusebio.addSports(List.of(futebol));
            usainBolt.addSports(List.of(athletics, futebol));

            em.persist(federer);
            em.persist(eusebio);
            em.persist(usainBolt);

            em.flush();

        });

        List<AthleteInfo> athleteInfos = provider.em()
                .createQuery("""
                            select  new io.costax.models.AthleteInfo( a.id, a.name )
                            from Athlete a 
                            order by a.name
                        """, AthleteInfo.class)
                .getResultList();

        athleteInfos.forEach(System.out::println);

        assertEquals(3, athleteInfos.size());
        assertEquals("Eusébio", athleteInfos.get(0).name());
        assertEquals("Federer", athleteInfos.get(1).name());
        assertEquals("Usain Bolt", athleteInfos.get(2).name());
    }
}