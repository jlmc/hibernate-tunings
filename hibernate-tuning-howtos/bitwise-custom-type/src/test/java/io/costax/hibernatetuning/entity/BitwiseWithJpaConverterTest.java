package io.costax.hibernatetuning.entity;


import io.costax.hibernatetuning.entity.options.Options;
import io.costax.rules.EntityManagerProvider;
import org.junit.*;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Ignore
public class BitwiseWithJpaConverterTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    //@Test
    @Before
    //@After
    public void t00_cleanup() {
        provider.doInTx(em -> {
            em.createQuery("delete from Operation ").executeUpdate();
        });
    }

    @Test
    public void t01_create_a_operations() {
        provider.doInTx(em -> {
            final Operation juncos = Operation.of(2L, "Juncos", PersonalOption.Option1);
            juncos.addOptionEE(EnterpriseOption.VIEW);
            juncos.addOptionEE(EnterpriseOption.PIVOT);

            em.persist(juncos);
        });


        provider.doIt(em -> {
            final Operation operation = em.find(Operation.class, 2L);

            assertNotNull(operation);
            assertNotNull(operation.getEnterpriseOptions());

            final Options<EnterpriseOption> enterpriseOptions = operation.getEnterpriseOptions();

            assertThat(enterpriseOptions.getValues(), hasSize(2));
            assertThat(enterpriseOptions.getValues(), containsInAnyOrder(EnterpriseOption.VIEW, EnterpriseOption.PIVOT));
        });

        provider.doInTx(em -> {
            final Operation juncos = Operation.of(2L, "Juncos", PersonalOption.Option1);

            juncos.addOptionEE(EnterpriseOption.DEC);
            juncos.addOptionEE(EnterpriseOption.OWNER);

            juncos.removeEE(EnterpriseOption.PIVOT);

        });


        provider.doIt(em -> {
            final Operation operation = em.find(Operation.class, 2L);

            assertNotNull(operation);
            assertNotNull(operation.getEnterpriseOptions());

            final Options<EnterpriseOption> enterpriseOptions = operation.getEnterpriseOptions();

            assertThat(enterpriseOptions.getValues(), hasSize(3));
            assertThat(enterpriseOptions.getValues(), containsInAnyOrder(
                    EnterpriseOption.DRC,
                    EnterpriseOption.VIEW,
                    EnterpriseOption.OWNER));
        });


    }
}
