package io.costax.hibernatetunings.arrays;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class StringArrayType implements UserType {

    private final int[] arrayTypes = new int[] { Types.ARRAY };

    @Override
    public int[] sqlTypes() {
        return arrayTypes;
    }

    @Override
    public Class returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(final Object x, final Object y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(final Object x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public Object nullSafeGet(final ResultSet rs,
                              final String[] names,
                              final SharedSessionContractImplementor session,
                              final Object owner)
            throws HibernateException, SQLException {

        // get the first column names
        if (names != null && names.length > 0 && rs != null && rs.getArray(names[0]) != null) {
            String[] results = (String[]) rs.getArray(names[0]).getArray();
            return results;
        }
        return null;
    }

    @Override
    public void nullSafeSet(final PreparedStatement st,
                            final Object value,
                            final int index,
                            final SharedSessionContractImplementor session)
            throws HibernateException, SQLException {

        st.setArray(index, st.getConnection().createArrayOf(
                "text",
                (String[]) value
        ));

        /*
        if (value != null && st != null) {

            String[] castObject = (String[]) value;
            Array array = session.connection().createArrayOf("text", castObject);
            st.setArray(index, array);

        } else {
            st.setNull(index, arrayTypes[0]);
        }*/
    }

    @Override
    public Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    @Override
    public Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object replace(final Object original, final Object target, final Object owner) throws HibernateException {
        return original;
    }
}
