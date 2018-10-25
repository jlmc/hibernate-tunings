package io.costax.hibernatetunning.customtypes;

import io.costa.hibernatetunings.entities.Machine;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;

public class CustomTypes {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void test() {
        provider.beginTransaction();

        Machine machine = Machine.of("ABC", Machine.Type.MAC, LocalDate.of(2017, Month.JANUARY, 3), LocalTime.of(14, 45));

        provider.em().persist(machine);

        provider.commitTransaction();

    }
}
