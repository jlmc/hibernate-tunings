package io.costax.relationships.generatortype;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CreatedByAndLastModifiedByUsingGeneratorTypeHibernateAnnotationTest {

    @JpaContext
    public JpaProvider provider;


    @Test
    public void should_persist_instance_checking_created_by_on_insert_and_update_by_on_update() {

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

        final Sensor ip = provider.doItWithReturn(em -> em.find(Sensor.class, 1));

        assertNotNull(ip);
        assertEquals("10.10.0.1", ip.getValue());
        assertEquals("Felix", ip.getCreatedBy());
        assertEquals("Archimedes", ip.getUpdatedBy());
    }
}