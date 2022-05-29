package io.costax.concurrency.pessimistic.bank;

import io.costax.concurrency.domain.bank.BankAccount;
import io.costax.concurrency.pessimistic.Utils;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static io.github.jlmc.jpa.test.annotation.Sql.Phase.AFTER_TEST_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
public class PessimisticLockInRealUseCasesOfPessimisticWriteTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PessimisticLockInRealUseCasesOfPessimisticWriteTest.class);

    @JpaContext
    public JpaProvider provider;

    /**
     * This test case intended to show a concurrency Problem. Two thread trying to manipulate the same records.
     * <p>
     * The scenario is as follows:
     * <p>
     * 1. Thread-1 - Load the BankAccount 1 and 2, sleeps for 5 seconds and move Money from the Account 1 to Account 2 and commit the transaction.
     * 2. Thread-2 - Load BankAccount 1, extract all the money of the Account 1 and sleep 3 seconds before commits the transaction.
     * Result: As result the amount in The bank account in the end of the two transactions is corrupted.
     * </p>
     */
    @Test
    @DisplayName("Problem arising from the manipulation of data concurrently without any control")
    public void problemArisingFromTheManipulationOfDataConcurrentlyWithoutAnyControl() {
        IllegalStateException illegalStateException = Assertions.assertThrows(
                IllegalStateException.class,
                this::demonstrationOfTheProblemOfManipulatingInformationInConcurrentlyWay);

        assertEquals("Some Concurrency problem happens the total after all operations should be only 200.00", illegalStateException.getMessage());
    }

    public void demonstrationOfTheProblemOfManipulatingInformationInConcurrentlyWay() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> submit1 = executorService.submit(() ->
                provider
                        .doInTx(em -> {

                            BankAccount bankAccount1 = em.find(BankAccount.class, 1L);
                            BankAccount bankAccount2 = em.find(BankAccount.class, 2L);

                            Utils.sleepMilliseconds(5_000L);

                            // extract all the money
                            BigDecimal extractMoney = bankAccount1.extractMoney(bankAccount1.getAmount());
                            bankAccount2.addMoney(extractMoney);

                            em.flush();
                        }));

        Future<?> submit2 = executorService.submit(() ->
                provider
                        .doInTx(em -> {

                            //  Load BankAccount 1, extract all the money of the Account 1 and sleep 3 seconds before commits the transaction.
                            Utils.sleepMilliseconds(2_000L);

                            BankAccount bankAccount1 = em.find(BankAccount.class, 1L);
                            bankAccount1.extractMoney(new BigDecimal("50.00"));

                            em.flush();

                            Utils.sleepMilliseconds(3_000L);
                        }));

        // -------------------
        try {

            submit1.get();
            submit2.get();

        } catch (ExecutionException | InterruptedException e) {
            throw e;
        }

        LOGGER.info("======> Check kResults");
        EntityManager em = provider.em();
        BigDecimal total = em.createQuery("select sum(b.amount) from BankAccount b", BigDecimal.class).getSingleResult();

        BankAccount bankAccount1 = em.find(BankAccount.class, 1L);
        BankAccount bankAccount2 = em.find(BankAccount.class, 2L);

        if (total.compareTo(new BigDecimal("300.00")) >= 0) {
            LOGGER.info("Some Concurrency problem happens the total after all operations should be only 200.00");
            throw new IllegalStateException("Some Concurrency problem happens the total after all operations should be only 200.00");
        }
    }

    /**
     * Solution: It is necessary lock the account 1 and 2 for any king of operations until the transaction of the thread 1 ends.
     * PESSIMISTIC_WRITE:
     */
    @Test
    @Sql(statements = {
            "delete from bank.bank_account where code in (8881, 8882)",
            "insert into bank.bank_account(code, number, amount) values (8881, '001', 100.0)",
            "insert into bank.bank_account(code, number, amount) values (8882, '002', 200.0)"
    })
    @Sql(statements = "delete from bank.bank_account where code in (8881, 8882)", phase = AFTER_TEST_METHOD)
    @DisplayName("Solution 1:  Manipulation of data concurrently With LockModeType.PESSIMISTIC_WRITE")
    public void solutionWithPessimisticWriteInAllOperationsThat() throws Exception {

        final Long bankAccountSourceId = 8881L;
        final Long bankAccountTargetId = 8882L;

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Future<?> txMoveAllMoneyFromAccount1ToAccount2 = executorService.submit(() ->
                provider
                        .doInTx(em -> {
                            System.out.printf("===> TX-1 -> Load Account[%d] And Account[%d]\n", bankAccountSourceId, bankAccountTargetId);
                            BankAccount bankAccount1 = em.find(BankAccount.class, bankAccountSourceId, LockModeType.PESSIMISTIC_WRITE);
                            BankAccount bankAccount2 = em.find(BankAccount.class, bankAccountTargetId, LockModeType.PESSIMISTIC_WRITE);

                            Utils.sleepMilliseconds(5_000L);

                            System.out.printf("===> TX-1 -> Move the money from Account[%d] -> Account[%d]\n", bankAccountSourceId, bankAccountTargetId);

                            // extract all the money
                            BigDecimal extractMoney = bankAccount1.extractMoney(new BigDecimal("100.0"));
                            bankAccount2.addMoney(extractMoney);

                            em.flush();
                        }));

        Future<?> txExtractMoneyFromAccount1 = executorService.submit(() ->
                provider
                        .doInTx(em -> {

                            Utils.sleepMilliseconds(2_000L);
                            //  Load BankAccount 1, extract all the money of the Account 1 and sleep 3 seconds before commits the transaction.

                            System.out.printf("===> TX-2 -> Load Account[%d]\n", bankAccountSourceId);
                            BankAccount bankAccount1 = em.find(BankAccount.class, bankAccountSourceId, LockModeType.PESSIMISTIC_WRITE);

                            System.out.printf("===> TX-2 -> Extract '50' from Account[%d]\n", bankAccountSourceId);
                            bankAccount1.extractMoney(new BigDecimal("50.00"));
                            em.flush();

                        }));


        /// -----------------
        try {

            txMoveAllMoneyFromAccount1ToAccount2.get();
            txExtractMoneyFromAccount1.get();

        } catch (ExecutionException | InterruptedException e) {
            System.out.println("---");
            final Throwable cause = e.getCause();
            Assertions.assertNotNull(e.getCause());
            Assertions.assertTrue(IllegalStateException.class.isAssignableFrom(e.getCause().getClass()));
            assertEquals("The current account [8881] contain only [0.000000] that is less than the required value [50.000000]", e.getCause().getMessage());
        }

        LOGGER.info("======> Check kResults");
        EntityManager em = provider.em();

        BigDecimal total = em
                .createQuery("select sum(b.amount) from BankAccount b where b.id in ( :id )", BigDecimal.class)
                .setParameter("id", List.of(bankAccountSourceId, bankAccountTargetId))
                .getSingleResult();

        BankAccount bankAccount1 = em.find(BankAccount.class, bankAccountSourceId);
        BankAccount bankAccount2 = em.find(BankAccount.class, bankAccountTargetId);

        em.close();

        assertEquals(0, total.compareTo(new BigDecimal("300.00")));
        assertEquals(0, bankAccount1.getAmount().compareTo(BigDecimal.ZERO));
        assertEquals(0, bankAccount2.getAmount().compareTo(new BigDecimal("300.00")));

    }

}
