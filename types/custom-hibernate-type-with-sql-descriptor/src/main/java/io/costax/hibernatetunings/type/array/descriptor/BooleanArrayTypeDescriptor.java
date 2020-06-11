package io.costax.hibernatetunings.type.array.descriptor;

import io.costax.hibernatetunings.type.array.descriptor.internal.AbstractArrayTypeDescriptor;

import java.util.Arrays;

public class BooleanArrayTypeDescriptor extends AbstractArrayTypeDescriptor<boolean[]> {

    public static final BooleanArrayTypeDescriptor INSTANCE = new BooleanArrayTypeDescriptor();

    public BooleanArrayTypeDescriptor() {
        super(boolean[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "boolean";
    }

    @Override
    public String extractLoggableRepresentation(final boolean[] value) {
        return (value == null) ? "null" : Arrays.toString(value);
    }
}