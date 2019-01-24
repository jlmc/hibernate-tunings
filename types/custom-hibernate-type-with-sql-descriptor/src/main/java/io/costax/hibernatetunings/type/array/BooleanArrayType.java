package io.costax.hibernatetunings.type.array;

import io.costax.hibernatetunings.type.array.descriptor.BooleanArrayTypeDescriptor;
import io.costax.hibernatetunings.type.array.descriptor.internal.ArraySqlTypeDescriptor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Properties;

public class BooleanArrayType
        extends AbstractSingleColumnStandardBasicType<boolean[]>
        implements DynamicParameterizedType {

    public static final IntArrayType INSTANCE = new IntArrayType();

    public BooleanArrayType() {
        super(ArraySqlTypeDescriptor.INSTANCE, BooleanArrayTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return "boolean-array";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((BooleanArrayTypeDescriptor) getJavaTypeDescriptor())
                .setParameterValues(parameters);
    }
}