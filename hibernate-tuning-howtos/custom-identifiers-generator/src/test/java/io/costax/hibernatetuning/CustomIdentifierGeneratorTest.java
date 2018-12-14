package io.costax.hibernatetuning;

import io.costax.hibernatetuning.entity.Student;
import io.costax.rules.EntityManagerProvider;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomIdentifierGeneratorTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void shouldCreate() {
        provider.beginTransaction();

        Student student = new Student("Johny");

        provider.em().persist(student);

        provider.commitTransaction();
    }

    @Test
    public void zCleanUp() {
        provider.beginTransaction();
        provider.em().createQuery("delete from Student").executeUpdate();
        provider.commitTransaction();
    }
}