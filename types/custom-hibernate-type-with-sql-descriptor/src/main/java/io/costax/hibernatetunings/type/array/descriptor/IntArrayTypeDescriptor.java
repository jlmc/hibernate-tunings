package io.costax.hibernatetunings.type.array.descriptor;

import io.costax.hibernatetunings.type.array.descriptor.internal.AbstractArrayTypeDescriptor;

public class IntArrayTypeDescriptor extends AbstractArrayTypeDescriptor<int[]> {

    public static final IntArrayTypeDescriptor INSTANCE = new IntArrayTypeDescriptor();

    public IntArrayTypeDescriptor() {
        super(int[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "int";
    }
}