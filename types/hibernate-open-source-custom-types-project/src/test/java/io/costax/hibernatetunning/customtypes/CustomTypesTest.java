package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.entities.Machine;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalDate;
import java.time.Month;

@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = "delete from machine where true", phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = "delete from machine where true", phase = Sql.Phase.AFTER_TEST_METHOD)
public class CustomTypesTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void test() {
        provider.doInTx(em -> {
            Machine machine = Machine.of(Machine.Type.MAC, LocalDate.of(2017, Month.JANUARY, 2));
            em.persist(machine);
        });
    }

}
