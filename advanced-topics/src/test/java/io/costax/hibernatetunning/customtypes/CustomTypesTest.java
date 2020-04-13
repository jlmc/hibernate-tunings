package io.costax.hibernatetunning.customtypes;

import io.costax.hibernatetunings.customtype.IPv4;
import io.costax.hibernatetunings.customtype.MacAddr;
import io.costax.hibernatetunings.entities.Machine;
import io.costax.rules.EntityManagerProvider;
import org.junit.*;

import java.time.LocalDate;
import java.time.Month;

public class CustomTypesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @After
    public void after() {
        provider.doInTx(em -> {
            em.createQuery("delete from Machine ").executeUpdate();
        });
    }

    @Before
    public void before() {
        provider.doInTx(em -> {
            em.createQuery("delete from Machine ").executeUpdate();
        });
    }

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

            Assert.assertNotNull(machine.getLastKnowIp());
            //Assert.assertNotNull(machine.getLastKnowIp());
        });
    }
}
