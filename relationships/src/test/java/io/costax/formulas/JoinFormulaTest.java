package io.costax.formulas;

import io.costax.relationships.formulas.BankAccount;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hamcrest.number.BigDecimalCloseTo;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JoinFormulaTest {

    private static final Integer ROGER_ID = 1;
    private static final Integer SERENA_ID = 2;

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_generate_some_bank_accounts() {
        provider.doInTx(em -> {

            final BankAccount roger = BankAccount.of(ROGER_ID, "Roger");
            em.persist(roger);

            final BankAccount serena = BankAccount.of(SERENA_ID, "Serena");
            em.persist(serena);
        });

        // Add money to roger
        provider.doInTx(em -> {
            final BankAccount bankAccount = em.find(BankAccount.class, ROGER_ID);
            bankAccount.push(new BigDecimal("10000.91"));
            bankAccount.push(new BigDecimal("9999.09"));
        });


        // Remove money to roger
        provider.doInTx(em -> {
            final BankAccount bankAccount = em.find(BankAccount.class, ROGER_ID);
            bankAccount.pull(new BigDecimal("500.00"));
        });

        // add money to serena
        provider.doInTx(em -> {
            final BankAccount bankAccount = em.find(BankAccount.class, SERENA_ID);
            bankAccount.push(new BigDecimal("100.90"));
            bankAccount.push(new BigDecimal("50.00"));
        });


        // What the value of the account? And what is the last Movement?
        final EntityManager em = provider.em();
        final BankAccount bankAccount = em.find(BankAccount.class, ROGER_ID);

        assertThat(bankAccount.getBalance(), Matchers.comparesEqualTo(new BigDecimal("19500.00")));
        assertThat(bankAccount.getLastMovement(), notNullValue());
        assertThat(bankAccount.getLastMovement().getValue().compareTo(new BigDecimal("-500")), is(0));
        assertThat(bankAccount.getLastMovement().getValue(), is(BigDecimalCloseTo.closeTo(new BigDecimal("-500"), new BigDecimal("0.0001"))));
    }
}
