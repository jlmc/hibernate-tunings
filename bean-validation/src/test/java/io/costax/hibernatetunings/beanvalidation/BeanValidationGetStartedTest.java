package io.costax.hibernatetunings.beanvalidation;

import io.costax.hibernatetunings.entities.Video;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
public class BeanValidationGetStartedTest {

    @JpaContext
    public JpaProvider provider;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    @Order(0)
    @DisplayName("Simple Validation In Persist")
    public void beanValidationValidationInPersist() {
        provider.doInTx(em -> {
            final Video bv = Video.of(1, "how to start-up bean validation with hibernate", "Entities annotated with constraints");
            em.persist(bv);
        });
    }


    @Test
    @Order(1)
    @DisplayName("Bean Validation validates Before Any Sql Statement execution")
    public void beanValidationValidatesBeforeAnySqlStatementExecution() {

        final EntityManager em = provider.em();
        try {
            em.getTransaction().begin();

            final Video bv = Video.of(1,
                    "how to start-up bean validation with hibernate",
                    null);

            em.persist(bv);
            em.flush();

            fail("The flush method execution should throw one Validation exception!!!!");

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();

            Assertions.assertTrue(e instanceof jakarta.validation.ConstraintViolationException);

        } finally {
            em.close();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Bean Validation Validates Before Sql Statement execution")
    public void beanValidationValidatesBeforeAnySqlStatement() {
        final EntityManager em = provider.em();

        try {

            em.getTransaction().begin();

            final Video bv = Video.of(1, "how to start-up bean validation with hibernate", null);

            em.persist(bv);
            em.flush();

            fail("should not execute, because the video.description should be validate by the Bean validation");

            em.getTransaction().commit();

        } catch (jakarta.validation.ConstraintViolationException e) {

            logger.info("--" + e.getMessage());
            logger.info("--" + e.getLocalizedMessage());

            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    @Order(3)
    @DisplayName("Fields without Bean validation Annotation always execute sl statement")
    public void fieldsWithoutBeanValidationAlwaysExecuteSqlStatement() {
        final EntityManager em = provider.em();

        try {
            em.getTransaction().begin();

            final Video bv = Video.of(1, null, "abc");

            em.persist(bv);

            em.getTransaction().commit();

            fail("Should throw one exception in the commit method execution!!!");

        } catch (Exception e) {

            assertTrue(e instanceof jakarta.persistence.RollbackException);
            final Throwable cause = e.getCause();
            assertTrue(cause instanceof PersistenceException);
            PersistenceException pe = (PersistenceException) e.getCause();
            assertTrue(pe.getCause() instanceof ConstraintViolationException);

            em.getTransaction().rollback();
        } finally {
            em.close();
        }
    }

    @Test
    @Order(4)
    @DisplayName("Using different validation groups in persist and update operations")
    public void performDifferentValidationGroupsInPersistAndUpdate() {
        // persist the example video
        provider.doInTx(em -> {

            final Video livVsNor = Video.of(20191, "Liv vs Nor", "First game of primer league 2019/2020");
            em.persist(livVsNor);
            em.flush();

        });

        // update the example video
        provider.doInTx(em -> {

            Video livVsNor = em.find(Video.class, 20191);
            livVsNor.publish(101);
            em.flush();

        });

    }

    @Test
    @Order(5)
    @DisplayName("Using Different Validation Groups in Persist And Update with constraint violations")
    public void performDifferentValidationGroupInPersistAndUpdateWithConstraintViolation() {

        // persist the example video
        provider.doInTx(em -> {

            final Video barVsNap = Video.of(20192, "Bar vs Nap", "Pre session game2019/2020");
            em.persist(barVsNap);
            em.flush();

        });

        // update the example video
        try {
            provider.doInTx(em -> {

                Video livVsNor = em.find(Video.class, 20192);
                livVsNor.publish(99);
                //em.flush();

            });

            fail("The Update transaction should throw one exception!!!");

        } catch (jakarta.validation.ConstraintViolationException e) {
            //System.out.println(e);
        }

    }
}
