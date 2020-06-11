package io.costax.hibernatetunning.identifiers;

import io.costax.hibernatetunings.entities.Developer;
import io.costax.hibernatetunings.entities.TimePeriod;
import io.costax.hibernatetunings.entities.Timesheet;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.*;
import java.util.List;

@JpaTest(persistenceUnit = "it")
public class SequencePooledGeneratorTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SequencePooledGeneratorTest.class);

    @PersistenceContext
    public EntityManager em;

    @Test
    public void createTheTimeSheets() {
        /*
        select nextval('timesheet_id_sequence');
        alter sequence timesheet_id_sequence restart;
        delete from timesheet;
        */

        final ZoneOffset offset = OffsetDateTime.now().getOffset();
        OffsetDateTime jan = OffsetDateTime.of(
                LocalDateTime.of(
                        LocalDate.of(2019, Month.JANUARY, 1),
                        LocalTime.of(8, 0)),
                offset);


        em.getTransaction().begin();

        final List<Developer> gangOfFour = em.createQuery("from Developer d order by d.id", Developer.class).getResultList();


        for (int i = 0; i < 4; i++) {

            OffsetDateTime start = jan.plusWeeks(i);

            for (Developer d : gangOfFour) {

                final TimePeriod first = TimePeriod.of(start, start.plusHours(8));
                final TimePeriod second = first.plusDays(1L);
                final TimePeriod third = first.plusDays(2L);
                final TimePeriod fourth = first.plusDays(3L);
                final TimePeriod fifth = first.plusDays(4L);

                final Timesheet workingDay1 = Timesheet.of(d, first);
                final Timesheet workingDay2 = Timesheet.of(d, second);
                final Timesheet workingDay3 = Timesheet.of(d, third);
                final Timesheet workingDay4 = Timesheet.of(d, fourth);
                final Timesheet workingDay5 = Timesheet.of(d, fifth);

                /*
                 * We can know what the Id will be before the insert happens
                 */
                LOGGER.info("--- Start Persisting: [{}, {}] - of {}",
                        workingDay1.getTimePeriod().getStart(),
                        workingDay1.getTimePeriod().getUntil(),
                        workingDay1.getDeveloper().getNome()
                );

                em.persist(workingDay1);
                em.persist(workingDay2);
                em.persist(workingDay3);
                em.persist(workingDay4);
                em.persist(workingDay5);

                LOGGER.info("--- Ends of Persisting: [{}, {}] - of {}",
                        workingDay1.getTimePeriod().getStart(),
                        workingDay1.getTimePeriod().getUntil(),
                        workingDay1.getDeveloper().getNome()
                );
            }
        }

        em.getTransaction().commit();
    }

    @Test
    public void createTheGangOfFourDevelopers() {
        em.getTransaction().begin();

        final Developer erichGamma = new Developer.Builder().setNome("Erich Gamma").createDeveloper();
        final Developer richardHelm = new Developer.Builder().setNome("Richard Helm").createDeveloper();
        final Developer ralphJohnson = new Developer.Builder().setNome("Ralph Johnson").createDeveloper();
        final Developer johnVlissides = new Developer.Builder().setNome("John Vlissidesn").createDeveloper();
        // final Developer kentBeck = new Developer.Builder().setNome("Kent Beck").createDeveloper();

        em.persist(erichGamma);
        em.persist(richardHelm);
        em.persist(ralphJohnson);
        em.persist(johnVlissides);

        em.getTransaction().commit();
    }
}
