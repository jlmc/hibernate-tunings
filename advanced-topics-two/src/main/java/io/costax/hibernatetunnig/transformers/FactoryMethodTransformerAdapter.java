package io.costax.hibernatetunnig.transformers;

import org.hibernate.transform.BasicTransformerAdapter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public class FactoryMethodTransformerAdapter extends BasicTransformerAdapter {

    private static final Map<Method, FactoryMethodTransformerAdapter> CACHE = new WeakHashMap<>();
    private final Method factoryMethod;

    private FactoryMethodTransformerAdapter(final Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    public static FactoryMethodTransformerAdapter of(final Class<?> declaringClass, final String factoryMethodName) {
        Objects.requireNonNull(declaringClass);
        Objects.requireNonNull(factoryMethodName);

        Method factoryMethod = Arrays.stream(declaringClass.getDeclaredMethods())
                .filter(method -> factoryMethodName.equals(method.getName()))
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .findFirst()
                .map(method -> {
                    //if (!Modifier.isPublic(method.getModifiers())) {
                        method.setAccessible(true);
                    //}
                    return method;
                })
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("No static factory method with the name [%s] found in the class [%s]",
                                factoryMethodName, declaringClass.getName())));

        if (!CACHE.containsKey(factoryMethod)) {
            CACHE.put(factoryMethod, new FactoryMethodTransformerAdapter(factoryMethod));
        }

        return CACHE.get(factoryMethod);
    }

    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        try {
            return factoryMethod.invoke(null, tuple);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(String.format("Can't transformTuple because [%s]", e.getMessage()), e);
        }
    }

}
