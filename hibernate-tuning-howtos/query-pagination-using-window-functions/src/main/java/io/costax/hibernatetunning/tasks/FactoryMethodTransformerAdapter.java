package io.costax.hibernatetunning.tasks;

import org.hibernate.transform.BasicTransformerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class FactoryMethodTransformerAdapter extends BasicTransformerAdapter {

    private final Method factoryMethod;

    private FactoryMethodTransformerAdapter(final Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public static FactoryMethodTransformerAdapter of(final Class<?> factoryMethodClassEnclose, final String factoryMethodName) {
        Method factoryMethod = Arrays.stream(factoryMethodClassEnclose.getDeclaredMethods())
                .filter(method -> factoryMethodName.equals(method.getName()))
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .findFirst()
                .map(method -> {
                    if (!Modifier.isPublic(method.getModifiers())) {
                        method.setAccessible(true);
                    }
                    return method;
                })
                .orElseThrow(() -> new IllegalArgumentException("No static factory method with the name ["
                        + factoryMethodName +
                        "] found in the class [" + factoryMethodClassEnclose.getName() + "]"));

        return new FactoryMethodTransformerAdapter(factoryMethod);
    }

    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        try {
            return this.factoryMethod.invoke(null, tuple);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Can't transformTuple: " + e.getMessage(), e);
        }
    }

}
