package io.costax.hibernatetunning.functions;

import javax.persistence.EntityManager;
import java.util.function.Consumer;

@FunctionalInterface
public interface InJPAConsumer extends Consumer<EntityManager> {
}
