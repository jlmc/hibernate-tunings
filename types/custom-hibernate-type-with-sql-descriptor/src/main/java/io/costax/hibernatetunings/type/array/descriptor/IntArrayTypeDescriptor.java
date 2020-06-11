package io.costax.hibernatetunings.type.array.descriptor;

import io.costax.hibernatetunings.type.array.descriptor.internal.AbstractArrayTypeDescriptor;

import java.util.Arrays;

public class IntArrayTypeDescriptor extends AbstractArrayTypeDescriptor<int[]> {

    public static final IntArrayTypeDescriptor INSTANCE = new IntArrayTypeDescriptor();

    public IntArrayTypeDescriptor() {
        super(int[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "int";
    }

    @Override
    public String extractLoggableRepresentation(final int[] value) {
        // This method is used for logging, for example:
        // [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [2] as [ARRAY] - [[1,2,3]]
        // [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([numbers] : [ARRAY]) - [[1,2,3]]
        return (value == null) ? "null" : Arrays.toString(value);
    }
}