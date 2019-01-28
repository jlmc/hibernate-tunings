package io.costax.hibernatetuning.entity.options;

import io.costax.hibernatetuning.bitwise.Bitwiseable;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public class Options<E extends Enum<E> & Bitwiseable> {

    private EnumSet<E> values;

    protected Options(final EnumSet<E> enterpriseOptions) {
        this.values = enterpriseOptions;
    }

    public static <E extends Enum<E> & Bitwiseable> Options<E> empty(Class<E> elementType) {
        return new Options<>(EnumSet.noneOf(elementType));
    }

    public void add(E e) {
        this.values.add(e);
    }

    public void remove(E e) {
        this.values.remove(e);
    }

    public boolean contain(E e) {
        return this.values.contains(e);
    }

    public Set<E> getValues() {
        return Collections.unmodifiableSet(values);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Options<?> options = (Options<?>) o;
        return Objects.equals(values, options.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }
}
