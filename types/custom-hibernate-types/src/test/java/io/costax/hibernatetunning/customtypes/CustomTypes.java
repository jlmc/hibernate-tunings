package io.costax.hibernatetunning.customtypes;

import io.costa.hibernatetunings.customtype.IPv4;
import io.costa.hibernatetunings.entities.Machine;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.time.LocalDate;
import java.time.Month;

public class CustomTypes {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void test() {
        provider.beginTransaction();

        Machine machine = Machine.of("ABC", Machine.Type.MAC, LocalDate.of(2017, Month.JANUARY, 3));

        provider.em().persist(machine);

        provider.commitTransaction();
    }

    @Test
    public void updateLastIPAddress() {
        provider.beginTransaction();

        Machine machine = provider.em().find(Machine.class, 1L);
        machine.setLastKnowIp(IPv4.of("192.168.3.34"));

        provider.commitTransaction();
    }
}
