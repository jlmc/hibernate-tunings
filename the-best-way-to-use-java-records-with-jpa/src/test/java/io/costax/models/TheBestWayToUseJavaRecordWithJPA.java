package io.costax.models;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class TheBestWayToUseJavaRecordWithJPA {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider
            .withPersistenceUnit("it");

    @Test
    public void usingJavaRecordToFetchData() {

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

        assertThat(athleteInfos.size(), is(3));
        assertThat(athleteInfos.get(0).name(), is("Eusébio"));
        assertThat(athleteInfos.get(1).name(), is("Federer"));
        assertThat(athleteInfos.get(2).name(), is("Usain Bolt"));

    }
}