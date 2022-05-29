package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;

public class IntArrayType implements UserType<int[]> {

    @Override
    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<int[]> returnedClass() {
        return int[].class;
    }

    @Override
    public boolean equals(int[] x, int[] y) {
        return Arrays.equals(x, y);
    }

    @Override
    public int hashCode(int[] x) {
        return Arrays.hashCode(x);
    }

    @Override
    public int[] nullSafeGet(ResultSet rs,
                             int position,
                             SharedSessionContractImplementor session,
                             Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }

        //Object array1 = array.getArray();
        int[] unboxed =  ArraysUtils.asIntArray(array.getArray());
        return unboxed;
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            int[] value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value != null && st != null) {
            st.setArray(index,
                    st.getConnection()
                      .createArrayOf("int", ArraysUtils.asObjectArray(value)));
        } else if (st != null) {
            st.setNull(index, getSqlType());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public int[] deepCopy(int[] value) {
        return Arrays.copyOf(value, value.length);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(int[] value) {
        return value;
    }

    @Override
    public int[] assemble(Serializable cached, Object owner) {
        return (int[]) cached;
    }

    @Override
    public int[] replace(int[] detached, int[] managed, Object owner) {
        return detached;
    }
}
