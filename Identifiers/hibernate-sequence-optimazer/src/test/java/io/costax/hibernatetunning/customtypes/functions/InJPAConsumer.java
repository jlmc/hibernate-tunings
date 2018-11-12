package io.costax.hibernatetunning.customtypes.functions;

import javax.persistence.EntityManager;
import java.util.function.Consumer;

@FunctionalInterface
public interface InJPAConsumer extends Consumer<EntityManager> {
}
