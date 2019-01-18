package io.costa.hibernatetunings.arrays;

import io.costa.hibernatetunings.customtype.ImmutableType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ImmutableStringArrayType extends ImmutableType<String[]> {

    public ImmutableStringArrayType() {
        super(String[].class);
    }

    @Override
    protected String[] get(final ResultSet rs,
                           final String[] names,
                           final SharedSessionContractImplementor session,
                           final Object owner) throws SQLException {

        if (names != null && names.length > 0 && rs != null && rs.getArray(names[0]) != null) {
            String[] results = (String[]) rs.getArray(names[0]).getArray();
            return results;
        }

        return null;
    }

    @Override
    protected void set(final PreparedStatement st,
                       final String[] value,
                       final int index,
                       final SharedSessionContractImplementor session) throws SQLException {

        st.setArray(index, st.getConnection().createArrayOf(
                //"varchar", / if the type was "varchar[] then we should use: character varying[]
                "text",
                value
        ));
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.ARRAY};
    }
}
