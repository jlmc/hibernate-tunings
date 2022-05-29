package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

public class TextArrayType implements org.hibernate.usertype.UserType<String[]> {
    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<String[]> returnedClass() {
        return String[].class;
    }

    @Override
    public boolean equals(String[] x, String[] y) {
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(String[] x) {
        return Arrays.hashCode(x);
    }

    @Override
    public String[] nullSafeGet(ResultSet rs,
                                int position,
                                SharedSessionContractImplementor session,
                                Object owner) throws SQLException {

        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }

        Object array1 = array.getArray();
        return (String[]) array1;
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            String[] value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value != null && st != null) {
            Array array = st.getConnection().createArrayOf("text", value);
            st.setArray(index, array);
        } else if (st != null){
            st.setNull(index, getSqlType());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public String[] deepCopy(String[] value) {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(String[] value) {
        return value;
    }

    @Override
    public String[] assemble(Serializable cached, Object owner) {
        return (String[]) cached;
    }

    @Override
    public String[] replace(String[] detached, String[] managed, Object owner) {
        return detached;
    }
}
