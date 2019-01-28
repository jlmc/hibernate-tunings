package io.costax.hibernatetuning.entity;

import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BitwiseUserTypeTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    //@Before
    //@After
    public void t00_cleanup() {
        provider.doInTx(em -> {
            em.createQuery("delete from Operation ").executeUpdate();
        });
    }

    @Test
    public void t01_create_a_operations() {

        provider.doInTx(em -> {

            final Operation jojo = Operation.of(1L,
                    "Jojo",
                    PersonalOption.Option1, PersonalOption.Option2);

            em.persist(jojo);

        });
    }

    @Test
    public void t02_add_options() {
        provider.beginTransaction();

        final Operation jojo = provider.em().find(Operation.class, 1L);

        jojo.setName("EG. Jojo");

        jojo.addOption(PersonalOption.Option4);

        provider.em().flush();
        provider.commitTransaction();
    }

    @Test
    public void t02_add_and_remove_option() {
        provider.beginTransaction();

        final Operation jojo = provider.em().find(Operation.class, 1L);

        jojo.removeOption(PersonalOption.Option1);
        jojo.removeOption(PersonalOption.Option2);
        jojo.addOption(PersonalOption.Option3);

        provider.em().flush();
        provider.commitTransaction();
    }
}
