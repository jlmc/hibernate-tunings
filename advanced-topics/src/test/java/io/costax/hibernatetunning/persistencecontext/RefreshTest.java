package io.costax.hibernatetunning.persistencecontext;

import io.costa.hibernatetunings.entities.client.Client;
import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RefreshTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");


//    @Before
//    public void setUp() throws Exception {
//            final Session session = provider.em().unwrap(Session.class);
//            session.doWork(connection -> {
//                try (Statement statement = connection.createStatement()) {
//                    statement.executeUpdate("delete from public.client");
//                    //statement.executeUpdate("delete from public.article");
//                } catch (Exception ignore) {
//                }
//            });
//    }

    @Test
    public void a_createClient() {
        provider.beginTransaction();

        provider.em().createNativeQuery("delete from client c").executeUpdate();
        provider.em().flush();

        final EntityManager em = provider.em();

        final Client client = new Client(1, "AJX", "Aura-juno-xax");
        em.persist(client);

        assertNull(client.getCreatedOn());
        em.flush();
        assertNotNull(client.getCreatedOn());

        provider.commitTransaction();
    }

    @Test
    public void b_testUpdatedEntity() {
        provider.beginTransaction();
        final EntityManager entityManager = provider.em();

        Client post = entityManager.find(Client.class, 1);
        assertEquals(0, post.getVersion());

        entityManager.createQuery("update versioned Client set name = 'n/a' where slug = :slog")
                .setParameter("slog", "AJX")
                .executeUpdate();

        assertEquals(0, post.getVersion());
        assertEquals("Aura-juno-xax", post.getName());

        entityManager.refresh(post);

        assertEquals(1, post.getVersion());
        assertEquals("n/a", post.getName());

        provider.commitTransaction();
    }

    @Test
    public void z_removeAll() {
        provider.beginTransaction();
        provider.em().createNativeQuery("delete from client").executeUpdate();
        provider.commitTransaction();
    }

//
//
//    @Test
//    public void test() {
//        doInJPA(entityManager -> {
//            Post post = entityManager.find(Post.class, 1L);
//            post.setTitle("JPA and Hibernate");
//            entityManager.refresh(post);
//        });
//    }
}
