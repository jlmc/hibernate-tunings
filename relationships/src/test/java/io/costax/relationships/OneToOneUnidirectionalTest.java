package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;

public class OneToOneUnidirectionalTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void testPeriste() {
        provider.doInTx(em -> {

            final Person jc = new Person(11, "JC");
            em.persist(jc);

            //final PersonDetail details = new PersonDetail(12, jc, "Coimbra", LocalDate.of(1999, Month.JUNE, 1));
            final PersonDetail details = new PersonDetail(jc, "Coimbra", LocalDate.of(1999, Month.JUNE, 1));

            //jc.setDetail(details);
            em.persist(details);

        });

        logger.info("*****************");
        logger.info("*****************");
        logger.info("*****************");

        final Person person = provider.em().find(Person.class, 11);

        logger.info("*** Person [{}]", person);


        logger.info("*****************");
        logger.info("*****************");
        logger.info("*****************");

        final PersonDetail personDetail = provider.em().find(PersonDetail.class, 11);

        logger.info("*** PersonDetail [{}]", personDetail);

        logger.info("*****************");
        logger.info("*****************");
        logger.info("*****************");

        final PersonDetail result = provider.em()
                .createQuery("select pd from PersonDetail pd where pd.person.id = :_id", PersonDetail.class)
                .setParameter("_id", 11)
                .getSingleResult();

        logger.info("*** PersonDetail [{}]", result);
    }
}