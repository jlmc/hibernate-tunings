package io.costax.hibernatetuning;

import io.costax.hibernatetuning.entity.Student;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.github.jlmc.jpa.test.annotation.Sql.Phase.AFTER_TEST_METHOD;

@JpaTest(persistenceUnit = "it")
@Sql(phase = AFTER_TEST_METHOD, statements = "delete from Student where true")
public class CustomIdentifierGeneratorTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void shouldCreate() {
        final Student student = provider
                .doInTxWithReturn(em -> {
                    final Student johny = new Student("Johny");
                    em.persist(johny);
                    return johny;

                });

        Assertions.assertNotNull(student);
        Assertions.assertNotNull(student.getId());
        Assertions.assertTrue(student.getId().startsWith("AS-"));
        Assertions.assertEquals("Johny", student.getName());
    }

}