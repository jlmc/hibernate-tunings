package io.costax.hibernatetunning.persistencecontext;

import io.costax.hibernatetunings.entities.client.Client;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(value = MethodOrderer.Alphanumeric.class)
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RefreshTest {

    @JpaContext
    public JpaProvider provider;




    @Test
    public void a_createClient() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        em.createNativeQuery("delete from client c").executeUpdate();
        em.flush();


        final Client client = new Client(1, "AJX", "Aura-juno-xax");
        em.persist(client);

        assertNull(client.getCreatedOn());
        em.flush();
        assertNotNull(client.getCreatedOn());

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void b_testUpdatedEntity() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        Client post = em.find(Client.class, 1);
        assertEquals(0, post.getVersion());

        em.createQuery("update versioned Client set name = 'n/a' where slug = :slog")
                .setParameter("slog", "AJX")
                .executeUpdate();

        assertEquals(0, post.getVersion());
        assertEquals("Aura-juno-xax", post.getName());

        em.refresh(post);

        assertEquals(1, post.getVersion());
        assertEquals("n/a", post.getName());

        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void z_removeAll() {
        provider.doInTx(em -> em.createNativeQuery("delete from client").executeUpdate());
    }

}
