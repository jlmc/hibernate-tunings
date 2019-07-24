package io.costax.hibernatetunings.beanvalidation;

import io.costax.hibernatetunings.entities.Video;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BeanValidationGetStartedTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void t00__bean_validation_startup() {
        provider.doInTx(em -> {
            final Video bv = Video.of(1, "how to start-up bean validation with hibernate", "Entities annotated with constraints");
            em.persist(bv);
        });
    }


    @Test(expected = javax.validation.ConstraintViolationException.class)
    public void t01_beanvalidation_validate_before_any_sql_statement() {
        provider.beginTransaction();

        final Video bv = Video.of(1, "how to start-up bean validation with hibernate", null);

        final EntityManager em = provider.em();
        em.persist(bv);
        em.flush();
        //provider.commitTransaction();
    }

    @Test
    public void t02_beanvalidation_validate_before_any_sql_statement() {
        provider.beginTransaction();

        try {

            final Video bv = Video.of(1, "how to start-up bean validation with hibernate", null);

            final EntityManager em = provider.em();
            em.persist(bv);
            em.flush();

            Assert.fail("should not execute, because the video.description should be validate by the Bean validation");
        } catch (javax.validation.ConstraintViolationException e) {

            logger.info("--" + e.getMessage());
            logger.info("--" + e.getLocalizedMessage());

            provider.rollbackTransaction();
        }
    }

    @Test
    public void t03_fields_without_beam_validation_always_execute_sql_statement() {
       provider.beginTransaction();

        try {

            final Video bv = Video.of(1, null, "abc");

            final EntityManager em = provider.em();
            em.persist(bv);

            provider.commitTransaction();

            Assert.fail();

        } catch (Exception e) {

            Assert.assertTrue(e instanceof javax.persistence.RollbackException);
            final Throwable cause = e.getCause();
            Assert.assertTrue(cause instanceof PersistenceException);
            PersistenceException pe = (PersistenceException) e.getCause();
            Assert.assertTrue(pe.getCause() instanceof ConstraintViolationException);

            provider.rollbackTransaction();
        }
    }


}