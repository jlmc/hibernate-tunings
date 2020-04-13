package io.costax.hibernatetunning.persistencecontext;

import io.costax.hibernatetunings.entities.exchange.Tread;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.jpa.QueryHints;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

/**
 * this example should run with the following configurations
 * <p>
 * properties.put("hibernate.jdbc.batch_size", "25");
 * properties.put("hibernate.order_inserts", "true");
 * properties.put("hibernate.order_updates", "true");
 * properties.put("hibernate.jdbc.batch_versioned_data", "true");
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BatchProcessingTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    /**
     * Although we could manually flush the Persistnece context,
     * this is not necessary when using the default AUTO FlushModeType.
     * <p>
     * Therefore we just need to commit the JPA transaction and the
     * Persistence Context will be flushed.
     * <p>
     * So that all the pending changes can be synchronized with underlying database.
     * <p>
     * After flushing the PC automatically the JPA commit method will
     * trigger a database transaction commit, and we are going to start a new JPA Transaction.
     * <p>
     * In the End the PC is cleared to ensure that the size not grow indefinitely witch show down
     * the dirty checking metchnism and put pressure on the JVM Garbage collector task.
     */
    private void flush(EntityManager entityManager) {
        entityManager.getTransaction().commit();
        entityManager.getTransaction().begin();

        entityManager.clear();
    }

    /**
     * persist 50 financial movements entities using batch size of 25 entries.
     */
    @Test
    public void a_testFlushClearCommit() {

        final int entityCount = 50;
        final int batchSize = 25;

        Random random = new Random(100);

        final EntityManager em = provider.em();

        try {
            // beginning a new JPA transaction, this does not necessary start a new database transaction,
            // as Hibernate can delay the connection acquisition until there is a SQL statement that needs
            // to be executed.
            em.getTransaction().begin();


            for (int i = 0; i < entityCount; i++) {

                if (i > 0 && i % batchSize == 0) {
                    flush(em);
                }

                final short i1 = (short) random.nextInt(100);
                final short i2 = (short) (random.nextBoolean() ? 1 : -1);
                short value = (short) (i2 * i1);


                /*
                 * In this example the batch processing task just persists some entities,
                 * but it could include update and delete operations as well.
                 */
                Tread post = new Tread(value);
                em.persist(post);


            }

            // make sure that açç remaining changes are synchronized with the database
            em.getTransaction().commit();

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    @Test
    public void b_testReadOnly() {
        final EntityManager em = provider.em();

        List<Tread> treads = em.createQuery(
                "select p from Tread p", Tread.class)
                .setHint(QueryHints.HINT_READONLY, true)
                .getResultList();

        treads.forEach(System.out::println);
    }
}
