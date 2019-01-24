package io.costax.hibernatetunings.type.array;

import io.costax.hibernatetunings.type.array.descriptor.StringArrayTypeDescriptor;
import io.costax.hibernatetunings.type.array.descriptor.internal.ArraySqlTypeDescriptor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.usertype.DynamicParameterizedType;

import java.util.Properties;

public class StringArrayType
        extends AbstractSingleColumnStandardBasicType<String[]>
        implements DynamicParameterizedType {

    public static final StringArrayType INSTANCE = new StringArrayType();

    public StringArrayType() {
        super(ArraySqlTypeDescriptor.INSTANCE, StringArrayTypeDescriptor.INSTANCE);
    }

    public String getName() {
        return "string-array";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        ((StringArrayTypeDescriptor) getJavaTypeDescriptor())
                .setParameterValues(parameters);
    }
}
