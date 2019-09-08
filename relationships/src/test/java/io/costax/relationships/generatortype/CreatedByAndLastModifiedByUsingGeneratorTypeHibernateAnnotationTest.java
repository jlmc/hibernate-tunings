package io.costax.relationships.generatortype;

import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;

public class CreatedByAndLastModifiedByUsingGeneratorTypeHibernateAnnotationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatedByAndLastModifiedByUsingGeneratorTypeHibernateAnnotationTest.class);
    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");


    @Test
    public void should_persiste_created_by_on_insert_and_update_by_on_update() {

        LoggedUserThreadLocal.logIn("Felix");


        provider.doInTx(em -> {
            Sensor ip = new Sensor(1, "ip", "192.168.0.5");
            em.persist(ip);

            Sensor temperature = new Sensor(2, "temperature", "45");
            em.persist(temperature);

            em.flush();
        });

        LoggedUserThreadLocal.logOut();

        LoggedUserThreadLocal.logIn("Archimedes");

        provider.doInTx(em -> {
            final Sensor ip = em.find(Sensor.class, 1);
            ip.setValue("10.10.0.1");
        });

        LoggedUserThreadLocal.logOut();


        final EntityManager em = provider.em();
        final Sensor ip = em.find(Sensor.class, 1);

        Assert.assertThat(ip, Matchers.notNullValue());
        Assert.assertThat(ip.getValue(), Matchers.equalTo("10.10.0.1"));
        Assert.assertThat(ip.getCreatedBy(), Matchers.equalTo("Felix"));
        Assert.assertThat(ip.getUpdatedBy(), Matchers.equalTo("Archimedes"));
    }
}