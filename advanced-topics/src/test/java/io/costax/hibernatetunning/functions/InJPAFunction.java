package io.costax.hibernatetunning.functions;

import javax.persistence.EntityManager;
import java.util.function.Function;

@FunctionalInterface
public interface InJPAFunction<T> extends Function<EntityManager, T> {
}
