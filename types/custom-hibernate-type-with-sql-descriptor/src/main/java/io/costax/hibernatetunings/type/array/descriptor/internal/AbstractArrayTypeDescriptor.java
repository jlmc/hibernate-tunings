package io.costax.hibernatetunings.type.array.descriptor.internal;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.usertype.DynamicParameterizedType;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;

public abstract class AbstractArrayTypeDescriptor<T> extends AbstractTypeDescriptor<T> implements DynamicParameterizedType {

    private Class<T> arrayObjectClass;

    public AbstractArrayTypeDescriptor(Class<T> arrayObjectClass) {
        super(arrayObjectClass, (MutabilityPlan<T>) new MutableMutabilityPlan<Object>() {
            @Override
            protected T deepCopyNotNull(Object value) {
                return ArrayTypes.deepCopy(value);
            }
        });
        this.arrayObjectClass = arrayObjectClass;
    }

    protected abstract String getSqlArrayType();

    @Override
    public void setParameterValues(Properties parameters) {
        arrayObjectClass = ((ParameterType) parameters.get(PARAMETER_TYPE)).getReturnedClass();

    }

    @Override
    public boolean areEqual(Object one, Object another) {
        if (one == another) {
            return true;
        }
        if (one == null || another == null) {
            return false;
        }
        return ArrayTypes.isEquals(one, another);
    }

    @Override
    public String toString(Object value) {
        return Arrays.deepToString((Object[]) value);
    }

    @Override
    public T fromString(String string) {
        return ArrayTypes.fromString(string, arrayObjectClass);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
        return (X) ArrayTypes.wrapArray(value);
    }

    @Override
    public <X> T wrap(X value, WrapperOptions options) {
        if (value instanceof Array) {
            Array array = (Array) value;
            try {
                return ArrayTypes.unwrapArray((Object[]) array.getArray(), arrayObjectClass);
            } catch (SQLException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return (T) value;
    }
}
