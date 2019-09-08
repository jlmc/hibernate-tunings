package io.costax.relationships.onetoone;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;

public class OneToOneUnidirectionalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneToOneUnidirectionalTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

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

        LOGGER.info(" ***************** \n ***************** \n *****************");

        final Person person = provider.em().find(Person.class, 11);

        LOGGER.info("*** Person [{}]", person);

        LOGGER.info(" ***************** \n ***************** \n *****************");

        final PersonDetail personDetail = provider.em().find(PersonDetail.class, 11);

        LOGGER.info("*** PersonDetail [{}]", personDetail);

        LOGGER.info(" ***************** \n ***************** \n *****************");

        final PersonDetail result = provider.em()
                .createQuery("select pd from PersonDetail pd where pd.person.id = :_id", PersonDetail.class)
                .setParameter("_id", 11)
                .getSingleResult();

        LOGGER.info("*** PersonDetail [{}]", result);
    }
}