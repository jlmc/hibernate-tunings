package io.costax.hibernatetunings.type.array.descriptor.internal;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicBinder;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import java.sql.*;

public class ArraySqlTypeDescriptor implements SqlTypeDescriptor {

    public static final ArraySqlTypeDescriptor INSTANCE = new ArraySqlTypeDescriptor();

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public boolean canBeRemapped() {
        return true;
    }

    @Override
    public <X> ValueBinder<X> getBinder(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicBinder<X>(javaTypeDescriptor, this) {
            @Override
            protected void doBind(final PreparedStatement st,
                                  final X value,
                                  final int index,
                                  final WrapperOptions options) throws SQLException {
                AbstractArrayTypeDescriptor<Object> abstractArrayTypeDescriptor = (AbstractArrayTypeDescriptor<Object>) javaTypeDescriptor;
                st.setArray(index, st.getConnection().createArrayOf(
                        abstractArrayTypeDescriptor.getSqlArrayType(),
                        abstractArrayTypeDescriptor.unwrap(value, Object[].class, options)
                ));
            }

            @Override
            protected void doBind(final CallableStatement st,
                                  final X value,
                                  final String name,
                                  final WrapperOptions options)
                    throws SQLException {
                throw new UnsupportedOperationException("Binding by name is not supported!");
            }
        };
    }

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>(javaTypeDescriptor, this) {
            @Override
            protected X doExtract(final ResultSet rs,
                                  final String name,
                                  final WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(rs.getArray(name), options);
            }

            @Override
            protected X doExtract(final CallableStatement statement,
                                  final int index,
                                  final WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getArray(index), options);
            }

            @Override
            protected X doExtract(final CallableStatement statement,
                                  final String name,
                                  final WrapperOptions options) throws SQLException {
                return javaTypeDescriptor.wrap(statement.getArray(name), options);
            }
        };
    }



}
