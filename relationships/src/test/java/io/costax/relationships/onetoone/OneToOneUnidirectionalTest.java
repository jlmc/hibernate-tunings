package io.costax.relationships.onetoone;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.Month;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OneToOneUnidirectionalTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneToOneUnidirectionalTest.class);

    @JpaContext
    JpaProvider provider;

    @PersistenceContext
    EntityManager em;

    @Test
    public void test_persist() {
        provider.doInTx(em -> {

            final Person jc = new Person(11, "JC");
            em.persist(jc);

            //final PersonDetail details = new PersonDetail(12, jc, "Coimbra", LocalDate.of(1999, Month.JUNE, 1));
            final PersonDetail details = new PersonDetail(jc, "Coimbra", LocalDate.of(1999, Month.JUNE, 1));

            //jc.setDetail(details);
            em.persist(details);

        });

        LOGGER.info(" ***************** \n ***************** \n *****************");



        final Person person = em.find(Person.class, 11);

        LOGGER.info("*** Person [{}]", person);

        LOGGER.info(" ***************** \n ***************** \n *****************");

        final PersonDetail personDetail = em.find(PersonDetail.class, 11);

        LOGGER.info("*** PersonDetail [{}]", personDetail);

        LOGGER.info(" ***************** \n ***************** \n *****************");

        final PersonDetail result = provider.em()
                .createQuery("select pd from PersonDetail pd where pd.person.id = :_id", PersonDetail.class)
                .setParameter("_id", 11)
                .getSingleResult();

        LOGGER.info("*** PersonDetail [{}]", result);
    }
}
