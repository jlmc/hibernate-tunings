package io.costax.trading.finex.entity;

import io.costax.core.reflection.Reflections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

class AccountTest {

    @Test
    void shouldCreateAccountUsingReflection() {
        Account dukeAccount = Account.createNewAccount("Duke", BigDecimal.TEN);
        Account result = Reflections.copyOf(dukeAccount);
        Assertions.assertNotNull(result);
    }
}