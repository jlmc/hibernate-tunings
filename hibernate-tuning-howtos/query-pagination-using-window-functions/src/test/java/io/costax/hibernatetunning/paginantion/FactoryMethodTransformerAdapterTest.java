package io.costax.hibernatetunning.paginantion;

import io.costax.hibernatetunning.tasks.FactoryMethodTransformerAdapter;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

public class FactoryMethodTransformerAdapterTest {

    @Test
    public void should_create_a_not_null_instance() {
        @SuppressWarnings("UnnecessaryBoxing")
        Object[] tuple = {
                Integer.valueOf(1),
                new BigDecimal("3.6")

        };
        final String factoryMethodName = "of";
        final Class<?> resultType = Foo.class;

        final FactoryMethodTransformerAdapter transformerAdapter = FactoryMethodTransformerAdapter.of(resultType, factoryMethodName);
        final Object o = transformerAdapter.transformTuple(tuple, null);

        Assert.assertNotNull(o);
        Assert.assertTrue(resultType.isAssignableFrom(o.getClass()));
    }
}