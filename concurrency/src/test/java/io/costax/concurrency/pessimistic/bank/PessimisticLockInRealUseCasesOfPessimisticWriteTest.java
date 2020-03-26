package io.costax.concurrency.pessimistic.bank;

import io.costax.concurrency.domain.bank.BankAccount;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PessimisticLockInRealUseCasesOfPessimisticWriteTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(PessimisticLockInRealUseCasesOfPessimisticWriteTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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
    public void should_manipulate_the_amount_in_concurrent_way() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Some Concurrency problem happens the total after all operations should be only 200.00");


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> submit1 = executorService.submit(() -> this.moveMoneyFromBankAccount1ToBankAccount2(5_000L));
        Future<?> submit2 = executorService.submit(() -> this.extract50MoneyFromTheAccount1(3_000L));

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
     * It is necessary lock the account 1 and 2 for any king of operations until the transaction of the thread 1 ends
     */
    @Test
    public void solution_with_pessimistic_write() throws Exception {
        //expectedException.expect(IllegalStateException.class);
        //expectedException.expectMessage("Some Concurrency problem happens the total after all operations should be only 200.00");


        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<?> submit1 = executorService.submit(() -> this.moveMoneyFromBankAccount1ToBankAccount2WithLockForWrite(5_000L));
        Future<?> submit2 = executorService.submit(() -> this.extract50MoneyFromTheAccount1(3_000L));

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
    }

    private void moveMoneyFromBankAccount1ToBankAccount2WithLockForWrite(final long millis) {
        provider.doInTx(em -> {


            BankAccount bankAccount1 = em.find(BankAccount.class, 1L, LockModeType.PESSIMISTIC_WRITE);
            BankAccount bankAccount2 = em.find(BankAccount.class, 2L, LockModeType.PESSIMISTIC_WRITE);

            Utils.sleep(millis);

            // extract all the money
            BigDecimal extractMoney = bankAccount1.extractMoney(bankAccount1.getAmount());
            bankAccount2.addMoney(extractMoney);

            em.flush();
        });
    }

    private void moveMoneyFromBankAccount1ToBankAccount2(final long millis) {
        provider.doInTx(em -> {


            BankAccount bankAccount1 = em.find(BankAccount.class, 1L);
            BankAccount bankAccount2 = em.find(BankAccount.class, 2L);

            Utils.sleep(millis);

            // extract all the money
            BigDecimal extractMoney = bankAccount1.extractMoney(bankAccount1.getAmount());
            bankAccount2.addMoney(extractMoney);

            em.flush();
        });
    }

    private void extract50MoneyFromTheAccount1(final long millis) {

        provider.doInTx(em -> {

            //  Load BankAccount 1, extract all the money of the Account 1 and sleep 3 seconds before commits the transaction.

            BankAccount bankAccount1 = em.find(BankAccount.class, 1L);
            bankAccount1.extractMoney(new BigDecimal("50.00"));

            em.flush();

            Utils.sleep(millis);


        });


    }

}
