package io.costax.hibernatetunings.arrays;

import io.costax.hibernatetunings.customtype.ImmutableType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntArrayType extends ImmutableType<int[]> {

    public IntArrayType() {
        super(int[].class);
    }

    @Override
    protected int[] get(final ResultSet rs,
                        final String[] names,
                        final SharedSessionContractImplementor session,
                        final Object owner) throws SQLException {
        if (names != null && names.length > 0 && rs != null && rs.getArray(names[0]) != null) {
            Integer[] a = (Integer[]) rs.getArray(names[0]).getArray();

            return getIntArray(a);
        }

        return null;
    }

    @Override
    protected void set(final PreparedStatement st,
                       final int[] value,
                       final int index,
                       final SharedSessionContractImplementor session) throws SQLException {

        Object[] array = getObjectsArray(value);

        st.setArray(index, st.getConnection().createArrayOf(
                "int",
                array
        ));
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }

    private Object[] getObjectsArray(final int[] value) {
        if (value == null) {
            return null;
        }

        Object[] array = new Object[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = value[i];
        }

        return array;
    }

    private int[] getIntArray(final Integer[] a) {
        if (a == null) {
            return null;
        }

        int[] out = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = a[i];
        }
        return out;
    }
}
