package io.costax.relationships.onetoone;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Month;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OneToOneTraditionalBidirectionalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneToOneTraditionalBidirectionalTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    @Order(1)
    public void test_creation() {

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
    @Order(2)
    @DisplayName("How to fix OneToOne N+1 problem even with fetch Lazy with Using Maven hibernate-enhance-maven-plugin with the configuration enableLazyInitialization true")
    public void test_fetch_one() {

        /*
         * even with lazy Loading two queries will be executed.
         */
        final Festival festival =
                provider.doItWithReturn(em -> em.find(Festival.class, 5));

        LOGGER.info("****** Festival: [{}]", festival);

        // note that this is true and the exception is throw when the bytecode is enhanced
        Assertions.assertThrows(LazyInitializationException.class, () -> System.out.println(festival.getDetails().toString()));
    }

    @Test
    @Order(3)
    public void test_fetch_multiple() {

        /*
         * More than one query will be executed.
         */

        provider.doIt(em -> {

            LOGGER.info("Fetching Festival records");

            final List<Festival> festivals =
                    em.createQuery("select f from Festival f", Festival.class)
                            .getResultList();

            festivals.forEach(f -> LOGGER.info("**** Festival: [{}]", f));
        });

    }
}