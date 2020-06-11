package io.costax.hibernatetunings.type.array.descriptor;

import io.costax.hibernatetunings.type.array.descriptor.internal.AbstractArrayTypeDescriptor;

import java.util.Arrays;

public class StringArrayTypeDescriptor extends AbstractArrayTypeDescriptor<String[]> {

    public static final StringArrayTypeDescriptor INSTANCE = new StringArrayTypeDescriptor();

    public StringArrayTypeDescriptor() {
        super(String[].class);
    }

    @Override
    protected String getSqlArrayType() {
        return "text";
    }

    @Override
    public String extractLoggableRepresentation(final String[] value) {
        // This method is used for logging, for example:
        // [org.hibernate.type.descriptor.sql.BasicBinder] - binding parameter [2] as [ARRAY] - [[A1]]
        // [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([roles3_0_0_] : [ARRAY]) - [[A1]]
        return (value == null) ? "null" : Arrays.toString(value);
    }
}
