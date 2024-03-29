package io.costax.models.specification;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DatabaseGeneratedValueWithJPATest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseGeneratedValueWithJPATest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_persist_entity_with_generated_value() {
        EntityManager em = provider.em();
        em.getTransaction().begin();

        Occurrence occurrence = Occurrence.of("Roger Federer wins 2017 Wimbledon Championships");

        em.persist(occurrence);

        LOGGER.info(" -- Before the flush [{}]", occurrence);

        em.flush();

        LOGGER.info(" -- Before the commit [{}]", occurrence);

        em.getTransaction().commit();
        LOGGER.info(" -- After the commit [{}]", occurrence);

        //List<Occurrence> occurrences = em.createQuery("select t from Occurrence t ", Occurrence.class).getResultList();

        em.refresh(occurrence);
        LOGGER.info(" -- After the refresh [{}]", occurrence);

        em.getTransaction().begin();
        LOGGER.info(" -- Removing the Occurrence [{}]", occurrence);
        em.remove(occurrence);
        em.flush();
        em.getTransaction().commit();

        em.close();
    }
}
