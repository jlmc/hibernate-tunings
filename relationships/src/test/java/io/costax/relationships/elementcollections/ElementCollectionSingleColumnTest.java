package io.costax.relationships.elementcollections;

import io.costax.relationships.onetomany.Actor;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class ElementCollectionSingleColumnTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void t00__create_actor_with_language_enum() {
        provider.doInTx(em -> {

            final Actor johnTravolta = Actor.of(1, "John Travolta");

            johnTravolta.addLanguage(Language.English);
            johnTravolta.addLanguage(Language.Portuguese);
            johnTravolta.addLanguage(Language.Spanish);
            johnTravolta.addLanguage(Language.French);

            // perform all the pending inserts
            em.persist(johnTravolta);
            em.flush();

            johnTravolta.removeLanguage(Language.French);

        });
    }

    @Test
    public void t01_create_actor_with_prize() {
        provider.doInTx(em -> {

            final Actor samuelLJackson = Actor.of(2, "Samuel L. Jackson");

            samuelLJackson.addLanguage(Language.English);
            samuelLJackson.addLanguage(Language.German);

            // perform all the pending inserts
            em.persist(samuelLJackson);
            em.flush();

            final ZoneOffset currentOffSet = OffsetDateTime.now().getOffset();

            final Prize prize1 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2018, 1, 1, 15, 0), currentOffSet), new BigDecimal("9999.98"));
            final Prize prize2 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2018, 3, 31, 15, 0), currentOffSet), new BigDecimal("1567.98"));
            final Prize prize3 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2019, 3, 31, 15, 0), currentOffSet), new BigDecimal("9981182.98"));

            samuelLJackson.addPrize(prize1);
            samuelLJackson.addPrize(prize2);
            samuelLJackson.addPrize(prize3);

            em.flush();

            final Prize equalToPrize = Prize.of(OffsetDateTime.of(LocalDateTime.of(2018, 3, 31, 15, 0), currentOffSet), new BigDecimal("1567.98"));
            samuelLJackson.removePrize(equalToPrize);

            em.flush();
        });
    }

    @Test
    public void t02_create_tv_serie() {
        provider.doInTx(em -> {

            final TvSerie tvSerie = new TvSerie(1);
            tvSerie.putEpisode("A", "Alma");
            tvSerie.putEpisode("B", "Boa");

            em.persist(tvSerie);
        });
    }

    @Test
    public void t03_create_tv_serie_prize() {
        provider.doInTx(em -> {

            final ZoneOffset currentOffSet = OffsetDateTime.now().getOffset();
            final Prize prize1 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2018, 1, 1, 15, 0), currentOffSet), new BigDecimal("9999.98"));
            final Prize prize2 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2018, 3, 31, 15, 0), currentOffSet), new BigDecimal("1567.98"));
            final Prize prize3 = Prize.of(OffsetDateTime.of(LocalDateTime.of(2019, 3, 31, 15, 0), currentOffSet), new BigDecimal("9981182.98"));


            final TvSerie tvSerie = new TvSerie(2);
            tvSerie.putPrize("A", prize1);
            tvSerie.putPrize("B", prize2);

            em.persist(tvSerie);
        });
    }
}
