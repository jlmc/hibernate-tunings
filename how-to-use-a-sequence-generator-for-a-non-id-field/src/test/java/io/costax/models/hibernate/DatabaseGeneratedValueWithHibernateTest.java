package io.costax.models.hibernate;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.time.LocalDate;

public class DatabaseGeneratedValueWithHibernateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseGeneratedValueWithHibernateTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_persist_entity_with_generated_value() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        Athlete rogerFederer = Athlete.of("Roger Federer", LocalDate.parse("1981-08-08"));

        em.persist(rogerFederer);

        LOGGER.info("--Before the commit the transaction [{}]", rogerFederer);

        em.getTransaction().commit();

        LOGGER.info("-- After the commit the transaction [{}]", rogerFederer);

        System.out.println(">> The output  " + rogerFederer);
    }
}