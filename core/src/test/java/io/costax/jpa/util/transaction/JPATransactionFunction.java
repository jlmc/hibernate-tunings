package io.costax.jpa.util.transaction;

import javax.persistence.EntityManager;
import java.util.function.Function;

@FunctionalInterface
public interface JPATransactionFunction<T> extends Function<EntityManager, T> {

    default void beforeTransactionCompletion() {
    }

    default void afterTransactionCompletion() {

    }
}
