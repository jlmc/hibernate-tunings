package io.costax.concurrency.pessimistic.bank;

import io.costax.concurrency.domain.bank.BankAccount;
import io.costax.concurrency.domain.bank.Client;
import io.costax.rules.EntityManagerProvider;
import io.costax.rules.Watcher;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.costax.concurrency.pessimistic.bank.Utils.sleep;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * This test case intended to show a concurrency Problem. Two thread trying to manipulate the same records.
 */
public class PessimisticLockInRealUseCasesOfPessimisticReadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PessimisticLockInRealUseCasesOfPessimisticReadTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public Watcher watcher = Watcher.timer(LOGGER);

    /**
     * This test case intended to show a concurrency Problem. Two thread trying to manipulate the same records.
     * <p>
     * The scenario is as follows:
     * <p>
     * 1. Thread-1 which want to create a new client, and add it to an existing BankAccount with code 2.
     * 2. Thread-2 which will remove the bank account 2.
     * 3. Thread 1 when it comes time to effect your changes will have an exception, as bank account 2 has disappeared in the meantime.
     * </p>
     * <p>
     * The Thread-1 is the first to do some work, it starts by Load the BankAccount with the id {@code 2L},
     * this will execute the SQL Statement {@code select on the bank_accoun table on the ID = 2}, from that moment that record will be on PersistenceContext,
     * after this, Just to ensure that we can provoke que problem this thread sleep 5 seconds.
     * After that 5 seconds this thread thy to adds a new client to your client list and perform the commit of the transaction.
     * <br>
     * A second Thread-2 while Thread-1 is sleeping (it start by sleep 3 seconds), will removes the bank_account record with the ID 2 from the database.
     * <br>
     * The problem will ocurres when the thread-1 thy to commit all its operations, because while the Thread-1 was sleeping,
     * another process went to the database and removed the record. A record that should now be a foreign key.
     * </p>
     */
    @Test(expected = org.hibernate.exception.ConstraintViolationException.class)
    public void should_add_a_new_client_to_an_existing_bank_account() throws Throwable {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> submit1 = executorService.submit(this::updateBankAccount2WithNewClient);
        Future<?> submit2 = executorService.submit(this::deleteBankAccountWithTheId2);

        try {

            submit2.get();
            submit1.get();

        } catch (ExecutionException e) {
            PersistenceException persistenceException = (PersistenceException) e.getCause();
            org.hibernate.exception.ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceException.getCause();
            assertThat(constraintViolationException.getSQL(), is("insert into bank.bank_account_client (bank_account_code, client_code) values (?, ?)"));
            assertThat(constraintViolationException.getConstraintName(), is("bank_account_client_bank_account_code_fkey"));
            throw constraintViolationException;
        }

        Assert.fail("The runnable1#get method execution " +
                "should throw a ExecutionException with " +
                "javax.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException");
    }

    /**
     * PESSIMISTIC_READ (for share)
     * - any concurrent process can read the record, but can not do any write operation (update or delete)
     * - because there is already other process (current thread) using the records in a write operation.
     * - Any write operation must be locked until the current transaction ends
     */
    @Test
    public void solutions_to_the_problem_with_PESSIMISTIC_READ() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> submit1 = executorService.submit(this::updateBankAccount2WithNewClientWithLockPessimisticRead);
        Future<?> submit2 = executorService.submit(this::deleteBankAccountWithTheId2);

        try {

            submit2.get();
            submit1.get();

        } catch (ExecutionException e) {
            PersistenceException persistenceException = (PersistenceException) e.getCause();
            org.hibernate.exception.ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceException.getCause();

            //Assert.assertThat(constraintViolationException.getSQL(), Matchers.is("delete from bank.bank_account where code=?"));
            assertThat(constraintViolationException.getConstraintName(), is("bank_account_client_bank_account_code_fkey"));
            assertThat(constraintViolationException.getCause().getMessage(), is("ERROR: update or delete on table \"bank_account\" violates foreign key constraint \"bank_account_client_bank_account_code_fkey\" on table \"bank_account_client\"\n" +
                    "  Detail: Key (code)=(2) is still referenced from table \"bank_account_client\"."));
        }
    }

    /**
     * {@link LockModeType#PESSIMISTIC_READ} (for share)
     * - any concurrent process can read the record, but can not do any write operation (update or delete)
     * - because there is already other process using the records
     * - Any write operation must be locked until the current transaction ends
     */
    public void updateBankAccount2WithNewClientWithLockPessimisticRead() {
        provider.doInTx(em -> {
            Client mika = em.merge(Client.createClient("Mika"));
            em.flush();

            BankAccount bankAccount = em.find(BankAccount.class, 2L, LockModeType.PESSIMISTIC_READ);
            bankAccount.addClient(mika);

            sleep(5_000);

            em.flush();
        });
    }

    private void updateBankAccount2WithNewClient() {
        provider.doInTx(em -> {
            Client mika = em.merge(Client.createClient("Mika"));
            em.flush();

            BankAccount bankAccount = em.find(BankAccount.class, 2L);
            bankAccount.addClient(mika);

            sleep(6_000);

            em.flush();
        });
    }

    private void deleteBankAccountWithTheId2() {
        provider.doInTx(em -> {
            sleep(3_000L);

            em.createNativeQuery("delete from bank.bank_account where code = :code").setParameter("code", 2L).executeUpdate();
            em.flush();
        });
    }


}
