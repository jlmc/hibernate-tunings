package io.costax.hibernatetunings.type.array.descriptor.internal;

import java.util.Arrays;

//TODO: implements the primitive types, byte, float, double

public final class ArrayTypes {

    private ArrayTypes() {
        throw new AssertionError();
    }

    public static <T> T deepCopy(final Object value) {
        Class arrayType = value.getClass();

        if (int[].class.equals(arrayType)) {
            int[] array = (int[]) value;
            return (T) Arrays.copyOf(array, array.length);
        }

        if (short[].class.equals(arrayType)) {
            short[] array = (short[]) value;
            return (T) Arrays.copyOf(array, array.length);
        }

        if (long[].class.equals(arrayType)) {
            long[] array = (long[]) value;
            return (T) Arrays.copyOf(array, array.length);
        }

        if (boolean[].class.equals(arrayType)) {
            boolean[] array = (boolean[]) value;
            return (T) Arrays.copyOf(array, array.length);
        }

        Object[] array = (Object[]) value;
        return (T) Arrays.copyOf(array, array.length);
    }

    public static boolean isEquals(final Object one, final Object another) {
        if (one.getClass() != another.getClass()) {
            return false;
        }

        Class arrayType = one.getClass();

        if (int[].class.equals(arrayType)) {
            return Arrays.equals((int[]) one, (int[]) another);
        }

        if (short[].class.equals(arrayType)) {
            return Arrays.equals((short[]) one, (short[]) another);
        }

        if (long[].class.equals(arrayType)) {
            return Arrays.equals((long[]) one, (long[]) another);
        }

        if (boolean[].class.equals(arrayType)) {
            return Arrays.equals((boolean[]) one, (boolean[]) another);
        }

        return Arrays.equals((Object[]) one, (Object[]) another);
    }

    public static Object[] wrapArray(Object objectArray) {
        Class arrayClass = objectArray.getClass();

        if (int[].class.equals(arrayClass)) {
            int[] fromArray = (int[]) objectArray;
            Integer[] array = new Integer[fromArray.length];
            for (int i = 0; i < fromArray.length; i++) {
                array[i] = fromArray[i];
            }
            return array;
        }

        if (short[].class.equals(arrayClass)) {
            short[] fromArray = (short[]) objectArray;
            Short[] array = new Short[fromArray.length];
            for (int i = 0; i < fromArray.length; i++) {
                array[i] = fromArray[i];
            }
            return array;
        }

        if (long[].class.equals(arrayClass)) {
            long[] fromArray = (long[]) objectArray;
            Long[] array = new Long[fromArray.length];
            for (int i = 0; i < fromArray.length; i++) {
                array[i] = fromArray[i];
            }
            return array;
        }

        if (boolean[].class.equals(arrayClass)) {
            boolean[] fromArray = (boolean[]) objectArray;
            Boolean[] array = new Boolean[fromArray.length];
            for (int i = 0; i < fromArray.length; i++) {
                array[i] = fromArray[i];
            }
            return array;
        }

        return (Object[]) objectArray;
    }

    public static <T> T unwrapArray(Object[] objectArray, Class<T> arrayClass) {

        if (int[].class.equals(arrayClass)) {
            int[] array = new int[objectArray.length];
            for (int i = 0; i < objectArray.length; i++) {
                array[i] = objectArray[i] != null ? (Integer) objectArray[i] : 0;
            }
            return (T) array;
        }

        if (short[].class.equals(arrayClass)) {
            short[] array = new short[objectArray.length];
            for (int i = 0; i < objectArray.length; i++) {
                array[i] = objectArray[i] != null ? (Short) objectArray[i] : 0;
            }
            return (T) array;
        }

        if (long[].class.equals(arrayClass)) {
            long[] array = new long[objectArray.length];
            for (int i = 0; i < objectArray.length; i++) {
                array[i] = objectArray[i] != null ? (Long) objectArray[i] : 0L;
            }
            return (T) array;
        }

        if (boolean[].class.equals(arrayClass)) {
            boolean[] array = new boolean[objectArray.length];
            for (int i = 0; i < objectArray.length; i++) {
                array[i] = objectArray[i] != null ? (Boolean) objectArray[i] : Boolean.FALSE;
            }
            return (T) array;
        }

        return (T) objectArray;
    }

    public static <T> T fromString(final String string, final Class<T> arrayClass) {
        final String sa = string.replaceAll("[\\[\\]]", "");
        final String[] tokens = sa.split(",");

        final int length = tokens.length;

        if (int[].class.equals(arrayClass)) {
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = Integer.valueOf(tokens[i]);
            }
            return (T) array;
        }

        if (short[].class.equals(arrayClass)) {
            short[] array = new short[length];
            for (int i = 0; i < length; i++) {
                array[i] = Short.valueOf(tokens[i]);
            }
            return (T) array;
        }

        if (long[].class.equals(arrayClass)) {
            long[] array = new long[length];
            for (int i = 0; i < length; i++) {
                array[i] = Long.valueOf(tokens[i]);
            }
            return (T) array;
        }

        if (boolean[].class.equals(arrayClass)) {
            boolean[] array = new boolean[length];
            for (int i = 0; i < length; i++) {
                array[i] = Boolean.valueOf(tokens[i]);
            }
            return (T) array;
        }

        return (T) tokens;
    }
}
