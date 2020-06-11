package io.costax.hibernatetunnig.annotations;

import io.costax.hibernatetunnig.entities.Machine;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * {@link NotFound} in hibernate is used when entities are associated to each other by ManyToOne, OneToMany etc. Suppose joined subclass has no data related to any id due to some database inconsistency. And we do not want to throw error, in this case @NotFound helps us. If we use @NotFound, then for any id if there is no data in associated joined subclass, error will not be thrown.
 * {@link NotFound} has two action {@link NotFoundAction#IGNORE} and {@link NotFoundAction#EXCEPTION}.
 */
@JpaTest(persistenceUnit = "it")
public class NotFoundAnnotationTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void shouldFound() {
        final Machine machine = provider.doItWithReturn(em -> em.find(Machine.class, 1));

        assertNotNull(machine);
        assertNotNull(machine.getDeveloper());
    }

    @Test
    public void shouldIgnoreNotFoundRelationship() {
        final Machine machine = provider.doItWithReturn(em -> em.find(Machine.class, 5));

        assertNotNull(machine);
        assertNull(machine.getDeveloper());
    }
}