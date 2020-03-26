package io.costax.hibernatetunnig.annotations;

import io.costax.hibernatetunnig.entities.Machine;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * {@link NotFound} in hibernate is used when entities are associated to each other by ManyToOne, OneToMany etc. Suppose joined subclass has no data related to any id due to some database inconsistency. And we do not want to throw error, in this case @NotFound helps us. If we use @NotFound, then for any id if there is no data in associated joined subclass, error will not be thrown.
 * {@link NotFound} has two action {@link NotFoundAction#IGNORE} and {@link NotFoundAction#EXCEPTION}.
 */
public class NotFoundAnnotationTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void shouldFound() {
        final Machine machine = provider.em().find(Machine.class, 1);

        Assert.assertNotNull(machine);
        Assert.assertNotNull(machine.getDeveloper());
    }

    @Test
    public void shouldIgnoreNotFoundRelationship() {
        final Machine machine = provider.em().find(Machine.class, 5);

        Assert.assertNotNull(machine);
        Assert.assertNull(machine.getDeveloper());
    }
}