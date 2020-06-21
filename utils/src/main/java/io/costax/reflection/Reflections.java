package io.costax.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class Reflections {

    private static final ConcurrentHashMap<Class<?>, List<Field>> CLASS_FIELDS = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<?>, Constructor<?>> DEFAULT_NO_ARG_CONSTRUCTOR = new ConcurrentHashMap<>();

    private Reflections() {
    }

    public static <T> T newInstance(String className) {
        try {
            Class<T> clazz = getClass(className);

            final Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();

        } catch (InstantiationException e) {
            throw handleException(className, e);
        } catch (IllegalAccessException e) {
            throw handleException(className, e);
        } catch (NoSuchMethodException e) {
            throw handleException(className + "#getDeclaredConstructor", e);
        } catch (InvocationTargetException e) {
            throw handleException(className + "#newInstance()", e);
        }
    }

    public static <T> Class<T> getClass(String className) {
        try {
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw handleException(className, e);
        }
    }

    public static String getClassPackageName(String className) {
        Package classPackage = getClass(className).getPackage();

        return classPackage != null ? classPackage.getName() : null;
    }

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

        } catch (IllegalAccessException e) {
            throw handleException(source.getClass().getName(), e);
        } catch (InstantiationException e) {
            throw handleException(source.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw handleException(source.getClass().getName(), e);
        }
    }

    private static void setValue(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw handleException(field.getName(), e);
        }
    }

    private static Object getValue(Object object, Field field) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw handleException(field.getName(), e);
        }
    }

    private static Optional<Constructor<?>> getDefaultNoArgConstructor(Class<?> type) {
        try {

            if (!DEFAULT_NO_ARG_CONSTRUCTOR.containsKey(type)) {
                Constructor<?> defaultNoArgConstructor = type.getDeclaredConstructor();

                if (defaultNoArgConstructor != null) {
                    defaultNoArgConstructor.setAccessible(true);
                }

                DEFAULT_NO_ARG_CONSTRUCTOR.putIfAbsent(type, defaultNoArgConstructor);
            }

            Constructor<?> constructor = DEFAULT_NO_ARG_CONSTRUCTOR.get(type);


            return Optional.ofNullable(constructor);
        } catch (NoSuchMethodException e) {
            throw handleException(type.getName(), e);
        }
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

    private static IllegalArgumentException handleException(String className, InstantiationException e) {
        return new IllegalArgumentException("Couldn't instantiate class " + className, e);
    }

    private static IllegalArgumentException handleException(String className, ClassNotFoundException e) {
        return new IllegalArgumentException("Couldn't find class " + className, e);
    }

    private static IllegalArgumentException handleException(String methodName, InvocationTargetException e) {
        return new IllegalArgumentException("Couldn't invoke method " + methodName, e);
    }

    private static IllegalArgumentException handleException(String memberName, IllegalAccessException e) {
        return new IllegalArgumentException("Couldn't access member " + memberName, e);
    }

    private static IllegalArgumentException handleException(String methodName, NoSuchMethodException e) {
        return new IllegalArgumentException("Couldn't find method " + methodName, e);
    }

    private static IllegalArgumentException handleException(String fieldName, NoSuchFieldException e) {
        return new IllegalArgumentException("Couldn't find field " + fieldName, e);
    }
}
