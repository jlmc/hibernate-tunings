package io.costax.core.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class Reflections {

    private static final ConcurrentHashMap<Class<?>, List<Field>> CLASS_FIELDS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Constructor<?>> DEFAULT_NO_ARG_CONSTRUCTOR = new ConcurrentHashMap<>();

    public static void copyFields(final Object source, final Object target, final List<String> ignoredProperties) {
        final Class<?> type = source.getClass();
        List<Field> fields = getAllFields(type).stream().filter(field -> !ignoredProperties.contains(field.getName())).collect(Collectors.toList());
        for (Field field : fields) {
            Object o1 = getValue(source, field);
            setValue(field, target, o1);
        }
    }

    public static <T> T copyOf(final T source) {
        return copyOf(source, List.of());
    }

    public static <T> T copyOf(final T source, final List<String> ignoredProperties) {
        Objects.requireNonNull(source, "The source must be not null");
        Objects.requireNonNull(ignoredProperties, "The ignoredProperties must be not null");
        return createNewInstanceAndCopyProperties(source, ignoredProperties);
    }

    private static <T> T createNewInstanceAndCopyProperties(T source, List<String> ignoredProperties) {
        Class<?> type = source.getClass();

        try {
            Constructor<?> defaultNoArgConstructor = getDefaultNoArgConstructor(type).orElse(null);

            Object newObject = defaultNoArgConstructor.newInstance();
            copyFields(source, newObject, ignoredProperties);

            return (T) newObject;

        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();

            throw new IllegalArgumentException(e);
        }
    }

    private static void setValue(Field field, Object target, Object value) {

        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private static Object getValue(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private static Optional<Constructor<?>> getDefaultNoArgConstructor(Class<?> type) throws NoSuchMethodException {
        if (!DEFAULT_NO_ARG_CONSTRUCTOR.containsKey(type)) {
            Constructor<?> defaultNoArgConstructor = type.getDeclaredConstructor();

            if (defaultNoArgConstructor != null) {
                defaultNoArgConstructor.setAccessible(true);
            }

            DEFAULT_NO_ARG_CONSTRUCTOR.putIfAbsent(type, defaultNoArgConstructor);
        }

        Constructor<?> constructor = DEFAULT_NO_ARG_CONSTRUCTOR.get(type);


        return Optional.ofNullable(constructor);
    }

    private static List<Field> findAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            List<Field> superFields = findAllFields(type.getSuperclass());
            fields.addAll(superFields);
        }

        return fields;
    }

    private static List<Field> getAllFields(Class<?> type) {
        if (!CLASS_FIELDS.containsKey(type)) {
            List<Field> allFields = findAllFields(type)
                    .stream()
                    .map(field -> {
                        field.setAccessible(true);
                        return field;
                    })
                    .collect(Collectors.toList());


            CLASS_FIELDS.putIfAbsent(type, allFields);
        }

        return CLASS_FIELDS.get(type);
    }

    private Reflections() {}
}
