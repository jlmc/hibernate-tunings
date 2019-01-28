package io.costax.hibernatetuning.bitwise;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Bitwise<E extends Enum<E> & Bitwiseable> {

    private int value = 0;

    private Bitwise() {
    }

    public static <E extends Enum<E> & Bitwiseable> Bitwise<E> of(final int anInt) {
        final Bitwise bitwise = new Bitwise();
        bitwise.value = anInt;
        return bitwise;
    }

    public static <E extends Enum<E> & Bitwiseable> Bitwise<E> of(E e) {
        final Bitwise<E> eBitwise = new Bitwise<>();
        eBitwise.add(e);

        return eBitwise;
    }

    public static <E extends Enum<E> & Bitwiseable> Bitwise<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null) {
            throw new ClassCastException(elementType + " not an enum");
        }

        return Bitwise.of(0);
    }

    private static <E extends Enum<E>> E[] getUniverse(Class<E> elementType) {
        final E[] enumConstants = elementType.getEnumConstants();
        return enumConstants;
    }

    public Bitwise<E> add(final E e) {
        return Bitwise.of(this.value | e.getWise());
    }

    public Bitwise<E> add(final E... e) {
        Bitwise<E> acumulator = Bitwise.of(this.value);

        for (E e1 : e) {
            acumulator = acumulator.add(e1);
        }

        return acumulator;
    }

    public Bitwise remove(final E e) {
        return Bitwise.of(this.value & ~e.getWise());
    }

    public boolean contains(final E x) {
        return (x.getWise() & value) == x.getWise();
    }

    public int getValue() {
        return this.value;
    }

    public Set<E> values(final Class<E> elementType) {
        final E[] enumConstants = elementType.getEnumConstants();

        final EnumSet<E> es = EnumSet.noneOf(elementType);
        for (E enumConstant : enumConstants) {
            if (contains(enumConstant)) {
                es.add(enumConstant);
            }
        }
        return es;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Bitwise<?> bitwise = (Bitwise<?>) o;
        return value == bitwise.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
