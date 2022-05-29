package io.github.jlmc.types.usetypes;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ArraysUtils {

    static Object[] asObjectArray(int[] values) {
        if (values == null) {
            return null;
        }

        return IntStream.of(values).boxed().toArray(Object[]::new);
    }

    static Object[] asObjectArray(boolean[] values) {
        if (values == null) {
            return null;
        }

        Object[] results = new Object[values.length];
        for (int i = 0; i < values.length; i++) {
            results[i] = values[i];
        }
        return results;
    }

    public static int[] asIntArray(Object array) {
        if (array == null) return null;

        if (array instanceof Integer[] integers) {
            return Arrays.stream(integers).mapToInt(i -> i).toArray();
        }

        if (array instanceof int[] ints) {
            return Arrays.stream(ints).toArray();
        }

        throw new IllegalArgumentException("The type " + array.getClass() + "  can not be converted to a int[]");
    }

    public static boolean[] asBooleanArray(Object array) {
        if (array == null) return null;

        if (array instanceof Boolean[] booleans1) {
            boolean[] bs = new boolean[booleans1.length];
            for (int i = 0; i < booleans1.length; i++) {
                bs[i] = booleans1[i];
            }
            return bs;
        }

        if (array instanceof boolean[] booleans2) {
            return Arrays.copyOf(booleans2, booleans2.length);
        }

        throw new IllegalArgumentException("The type " + array.getClass() + "  can not be converted to a int[]");
    }

    public static <T> T deepCopy(T value) {
        if (value.getClass().isAssignableFrom(int[].class)) {
            int[] temp = (int[]) value;
            return (T) Arrays.copyOf(temp, temp.length);
        }
        if (value.getClass().isAssignableFrom(boolean[].class)) {
            boolean[] temp = (boolean[]) value;
            return (T) Arrays.copyOf(temp, temp.length);
        }
        if (value.getClass().isAssignableFrom(String[].class)) {
            String[] temp = (String[]) value;
            return (T) Arrays.copyOf(temp, temp.length);
        }
        if (value.getClass().isAssignableFrom(Object[].class)) {
            Object[] temp = (Object[]) value;
            return (T) Arrays.copyOf(temp, temp.length);
        }
        throw new IllegalArgumentException("Unsupported class: " + value.getClass().getName());
    }
}
