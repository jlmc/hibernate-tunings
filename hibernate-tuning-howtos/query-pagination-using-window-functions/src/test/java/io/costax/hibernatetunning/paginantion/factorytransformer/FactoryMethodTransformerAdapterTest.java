package io.costax.hibernatetunning.paginantion.factorytransformer;

import io.costax.hibernatetunning.tasks.FactoryMethodTransformerAdapter;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FactoryMethodTransformerAdapterTest {

    @Test
    public void should_create_a_not_null_instance() {
        @SuppressWarnings("UnnecessaryBoxing")
        Object[] tuple = {
                Integer.valueOf(1),
                new BigDecimal("123")
        };
        final String factoryMethodName = "of";
        final Class<?> resultType = Foo.class;

        final FactoryMethodTransformerAdapter transformerAdapter =
                FactoryMethodTransformerAdapter.of(resultType, factoryMethodName);
        final Object o = transformerAdapter.transformTuple(tuple, null);

        assertNotNull(o);
        assertTrue(resultType.isAssignableFrom(o.getClass()));
    }
}
