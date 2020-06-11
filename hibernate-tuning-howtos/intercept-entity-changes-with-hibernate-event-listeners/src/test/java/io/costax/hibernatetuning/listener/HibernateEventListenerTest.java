package io.costax.hibernatetuning.listener;

import io.costax.hibernatetuning.entities.Client;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HibernateEventListenerTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    @Order(0)
    public void create_Two_Clients() {
        provider.doInTx(em -> {
            em.createNativeQuery("delete from client c").executeUpdate();
            em.flush();

            final Client ptech = new Client(1, "Ptech", "Present-Technologies");
            em.persist(ptech);

            final Client nasa = new Client(2, "Nasa", "Nasa");
            em.persist(nasa);

            final Client google = new Client(3, "G", "Google");
            em.persist(google);
        });
    }

    @Test
    @Order(1)
    public void update_a_existing_client() {
        provider.doInTx(em -> {

            final Client nasa = em.find(Client.class, 2);
            nasa.setName("National Aeronautics and Space Administration");

        });
    }

    @Test
    @Order(2)
    public void delete_a_existing_client() {
        provider.doInTx(em -> {

            final Client nasa = em.getReference(Client.class, 2);
            em.remove(nasa);

        });
    }

    @Test
    @Order(3)
    public void delete_a_existing_client_using_native_query() {

        EntityManager em = provider.em();

        final Number before = (Number) em.createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        em.getTransaction().begin();
        em.createNativeQuery("delete from client where id = 1").executeUpdate();
        em.getTransaction().commit();

        final Number after = (Number) em.createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        em.close();

        System.out.println("---" + before);
        System.out.println("---" + after);
        Assertions.assertFalse(before.longValue() < after.longValue());
    }

    @Test
    @Order(4)
    public void should_delete_using_jpql() {
        final EntityManager em = provider.em();

        final Number before = (Number) em.createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        em.getTransaction().begin();

        em.createQuery("delete from Client where id = 3").executeUpdate();

        em.getTransaction().commit();

        final Number after = (Number) em.createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        System.out.println("---" + before);
        System.out.println("---" + after);
        Assertions.assertFalse(before.longValue() < after.longValue());
    }

    @Test
    @Order(5)
    public void drop_all() {
        provider.doInTx(em -> {
            em.createNativeQuery("delete from client_trace").executeUpdate();
            em.createNativeQuery("delete from client").executeUpdate();
        });

    }
}