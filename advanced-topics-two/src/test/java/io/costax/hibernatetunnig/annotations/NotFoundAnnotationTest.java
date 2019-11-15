package io.costax.hibernatetunnig.annotations;

import io.costax.hibernatetunnig.entities.Machine;
import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


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