package io.costax.concurrency.pessimistic.bank;

import io.costax.concurrency.domain.bank.BankAccount;
import io.costax.concurrency.domain.bank.Client;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PersistenceUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.costax.concurrency.pessimistic.Utils.sleepMilliseconds;
import static io.github.jlmc.jpa.test.annotation.Sql.Phase.AFTER_TEST_METHOD;
import static io.github.jlmc.jpa.test.annotation.Sql.Phase.BEFORE_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This test case intended to show a concurrency Problem. Two thread trying to manipulate the same records.
 */
@JpaTest(persistenceUnit = "it")
public class PessimisticLockInRealUseCasesOfPessimisticReadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PessimisticLockInRealUseCasesOfPessimisticReadTest.class);

    @PersistenceUnit
    EntityManagerFactory emf;

    @JpaContext
    JpaProvider provider;

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
    @Test
    @Sql(statements = {"insert into bank.bank_account(code, number, amount) values (990, '2019', 123.0)"}, phase = BEFORE_TEST_METHOD)
    @Sql(statements = {
            "delete from bank.bank_account_client where bank_account_code = 990",
            "delete from bank.client where name = 'MIKA-PROBLEM-990'",
            "delete from bank.bank_account where code = 990"
    },
            phase = AFTER_TEST_METHOD)
    @DisplayName("Show a concurrency Problem. Two thread trying to manipulate the same records")
    public void showTheConcurrencyProblem_TwoThreadTryingToManipulateTheSameRecords() throws Throwable {

        final Long bankAccountId = 990L;
        final String newClientName = "MIKA-PROBLEM-990";

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<?> executionAddNewClientToBanKAccount = executorService.submit(
                () -> provider
                        .doInTx(em -> {
                            Client client = em.merge(Client.createClient(newClientName));
                            em.flush();

                            BankAccount bankAccount = em.find(BankAccount.class, bankAccountId);
                            bankAccount.addClient(client);

                            sleepMilliseconds(6_000L);

                            em.flush();
                        }));

        Future<?> executionDeletingBanKAccountThatIsBlocked = executorService.submit(
                () -> provider
                        .doInTx(em -> {
                            sleepMilliseconds(3_000L);

                            em.createNativeQuery("delete from bank.bank_account where code = :code")
                                    .setParameter("code", bankAccountId)
                                    .executeUpdate();

                            em.flush();
                        }));

        try {
            executionDeletingBanKAccountThatIsBlocked.get();
            executionAddNewClientToBanKAccount.get();
        } catch (ExecutionException e) {
            PersistenceException persistenceException = (PersistenceException) e.getCause();
            org.hibernate.exception.ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceException.getCause();

            assertEquals(
                    "insert into bank.bank_account_client (bank_account_code, client_code) values (?, ?)",
                    constraintViolationException.getSQL());

            assertEquals(
                    "bank_account_client_bank_account_code_fkey",
                    constraintViolationException.getConstraintName());

            return;
        }

        fail("The runnable1#get method execution " +
                "should throw a ExecutionException with " +
                "jakarta.persistence.PersistenceException: org.hibernate.exception.ConstraintViolationException");
    }

    /**
     * {@link LockModeType#PESSIMISTIC_READ} (for share)
     * - any concurrent process can read the record, but can not do any write operation (update or delete)
     * - because there is already other process using the records
     * - Any write operation must be locked until the current transaction ends
     */
    @Test
    @Sql(statements = {"insert into bank.bank_account(code, number, amount) values (991, '2020', 200.0)"}, phase = BEFORE_TEST_METHOD)
    @Sql(statements = {
            "delete from bank.bank_account_client where bank_account_code = 991",
            "delete from bank.client where name = 'MIKA-SOLUTION-991'",
            "delete from bank.bank_account where code = 991"
    },
            phase = AFTER_TEST_METHOD)
    @DisplayName("Solution: The Tx-1 locks the record for any concurrent WRITE operation, READ operations are allow")
    public void solutionWithLockTypePessimisticRead() throws InterruptedException {

        final Long bankAccountId = 991L;
        final String newClientName = "MIKA-SOLUTION-991";

        ExecutorService executorService = Executors.newFixedThreadPool(2);


        Future<?> executionAddNewClientToBanKAccount = executorService.submit(() -> provider
                .doInTx(em -> {
                    LOGGER.info("TX1-Add_New_Client_To_Bank_Account {} -> create new Client", Thread.currentThread().getId());
                    Client mika = em.merge(Client.createClient(newClientName));
                    em.flush();

                    LOGGER.info("TX1-Add_New_Client_To_Bank_Account {} -> find Bank Account {}", Thread.currentThread().getId(), bankAccountId);
                    BankAccount bankAccount = em.find(BankAccount.class, bankAccountId, LockModeType.PESSIMISTIC_READ);
                    bankAccount.addClient(mika);

                    sleepMilliseconds(6_000L);

                    LOGGER.info("TX1-Add_New_Client_To_Bank_Account {} -> Commit", Thread.currentThread().getId());
                    em.flush();
                }));


        Future<?> executionDeletingBanKAccountThatIsBlocked = executorService.submit(() -> provider
                .doInTx(em -> {
                    LOGGER.info("TX2-Deleting_BanK_Account {} -> waiting", Thread.currentThread().getId());
                    sleepMilliseconds(3_000L);

                    LOGGER.info("TX2-Deleting_BanK_Account {} -> Can Find {}", Thread.currentThread().getId(), bankAccountId);
                    final BankAccount bankAccount = em.find(BankAccount.class, bankAccountId);

                    LOGGER.info("TX2-Deleting_BanK_Account {} -> Deleting Find {}", Thread.currentThread().getId(), bankAccountId);
                    em.createNativeQuery("delete from bank.bank_account where code = :code")
                            .setParameter("code", bankAccountId)
                            .executeUpdate();

                    LOGGER.info("TX2-Deleting_BanK_Account {} -> Try to commit, should fail", Thread.currentThread().getId());
                    em.flush();
                }));


        try {

            executionDeletingBanKAccountThatIsBlocked.get();
            executionAddNewClientToBanKAccount.get();

        } catch (ExecutionException e) {
            PersistenceException persistenceException = (PersistenceException) e.getCause();
            org.hibernate.exception.ConstraintViolationException constraintViolationException = (ConstraintViolationException) persistenceException.getCause();

            assertEquals("bank_account_client_bank_account_code_fkey", constraintViolationException.getConstraintName());
            assertEquals("ERROR: update or delete on table \"bank_account\" violates foreign key constraint \"bank_account_client_bank_account_code_fkey\" on table \"bank_account_client\"\n" +
                    "  Detail: Key (code)=(" + bankAccountId + ") is still referenced from table \"bank_account_client\".", constraintViolationException.getCause().getMessage());

            return;
        }

        Assertions.fail("expected exceptions");
    }
}
