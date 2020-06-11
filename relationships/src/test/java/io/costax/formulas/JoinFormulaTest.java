package io.costax.formulas;

import io.costax.relationships.formulas.BankAccount;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JoinFormulaTest {

    private static final Integer ROGER_ID = 1;
    private static final Integer SERENA_ID = 2;

    @JpaContext
    public JpaProvider provider;

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
        provider.doIt(em -> {

            final BankAccount bankAccount = em.find(BankAccount.class, ROGER_ID);

            Assertions.assertEquals(0, new BigDecimal("19500.00").compareTo(bankAccount.getBalance()));
            Assertions.assertNotNull(bankAccount.getLastMovement());
            Assertions.assertEquals(0, new BigDecimal("-500").compareTo(bankAccount.getLastMovement().getValue()));

        });
    }
}
