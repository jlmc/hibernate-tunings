package io.costax.hibernatetunings.type.array.descriptor;

import io.costax.hibernatetunings.type.array.descriptor.internal.AbstractArrayTypeDescriptor;

public class BooleanArrayTypeDescriptor extends AbstractArrayTypeDescriptor<boolean[]> {

    public static final BooleanArrayTypeDescriptor INSTANCE = new BooleanArrayTypeDescriptor();

    public BooleanArrayTypeDescriptor() {
        super(boolean[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "boolean";
    }
}