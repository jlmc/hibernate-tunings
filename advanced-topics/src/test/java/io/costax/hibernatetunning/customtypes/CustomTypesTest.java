package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.customtype.IPv4;
import io.costax.hibernatetunings.customtype.MacAddr;
import io.costax.hibernatetunings.entities.Machine;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static io.github.jlmc.jpa.test.annotation.Sql.Phase.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
@Sql(statements = "delete from Machine", phase = BEFORE_TEST_METHOD)
@Sql(statements = "delete from Machine", phase = AFTER_TEST_METHOD)
public class CustomTypesTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void test() {
        provider.doInTx(em -> {
            Machine machine = Machine.of("08:00:2b:01:02:03", Machine.Type.MAC, LocalDate.of(2017, Month.JANUARY, 3));
            em.persist(machine);
        });

        provider.doInTx(em -> {
            final MacAddr macAddr = MacAddr.of("08:00:2b:01:02:03");
            final Machine machine = em.createQuery("select m from Machine m where m.macAddress = :mac", Machine.class)
                    .setParameter("mac", macAddr)
                    .getSingleResult();

            machine.setLastKnowIp(IPv4.of("192.168.3.34"));
        });

        provider.doInTx(em -> {
            final MacAddr macAddr = MacAddr.of("08:00:2b:01:02:03");
            final Machine machine = em.createQuery("select m from Machine m where m.macAddress = :mac", Machine.class)
                    .setParameter("mac", macAddr)
                    .getSingleResult();

            assertNotNull(machine.getLastKnowIp());
        });
    }
}
