package io.costax.models.hibernate;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DatabaseGeneratedValueWithHibernateTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseGeneratedValueWithHibernateTest.class);

    @JpaContext
    public JpaProvider provider;

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

        em.close();
    }
}
