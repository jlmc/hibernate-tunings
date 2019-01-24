package io.costax.hibernatetunings.type.array;

import io.costax.hibernatetunings.type.array.descriptor.IntArrayTypeDescriptor;
import io.costax.hibernatetunings.type.array.descriptor.internal.ArraySqlTypeDescriptor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Properties;

public class IntArrayType
        extends AbstractSingleColumnStandardBasicType<int[]>
        implements DynamicParameterizedType {

    public static final IntArrayType INSTANCE = new IntArrayType();

    public IntArrayType() {
        super(ArraySqlTypeDescriptor.INSTANCE, IntArrayTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return "int-array";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((IntArrayTypeDescriptor) getJavaTypeDescriptor())
                .setParameterValues(parameters);
    }
}
