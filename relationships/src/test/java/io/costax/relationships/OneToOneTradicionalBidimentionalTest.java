package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class OneToOneTradicionalBidimentionalTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testCreation() {

        provider.doInTx(em -> {

            final ZoneOffset offset = OffsetDateTime.now().getOffset();
            // 6 a 21 de agosto

            final OffsetDateTime happensAt = OffsetDateTime.of(2019, Month.AUGUST.getValue(), 6, 16, 0, 0, 0, offset);

            final Festival festival = Festival.createFestival(1, "Festival Internacional de Cinema de Veneza");

            final FestivalDetails festivalDetail = new FestivalDetails(2,
                    festival,
                    "Italy",
                    "Veneza",
                    happensAt);

            festival.setDetails(festivalDetail);

            em.persist(festival);
            //em.getTransaction().commit();
            //em.getTransaction().begin();

            em.flush();


            // change the details

            final FestivalDetails festivalDetail2 = new FestivalDetails(3,
                    festival,
                    "Italy",
                    "Veneza - Orizzonti section",
                    happensAt);

            //festival.setFestivalDetails(null);
            //em.flush();
            festival.setDetails(festivalDetail2);
        });
    }

    @Test
    public void testFetchOne() {

        /*
         * even with lazy Loading two queries will be executed.
         */
        final EntityManager em = provider.em();
        final Festival festival = em.find(Festival.class, 5);

        logger.info("****** Festival: [{}]", festival);

        logger.info("****** Festival-Details: [{}]", festival.getDetails());
    }

    @Test
    public void testFetchMultiple() {

        /*
         * More than one query will be executed.
         */

        final List<Festival> festivals = provider.em().createQuery("select f from Festival f", Festival.class).getResultList();

        festivals.forEach(f -> logger.info("**** Festival: [{}]", f));
    }
}