package io.costax.hibernatetuning.bitwise.type;

import io.costax.hibernatetuning.bitwise.Bitwise;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class BitwiseType extends BaseType<Bitwise> {

    public BitwiseType() {
        super(Bitwise.class);
    }

    @Override
    protected Bitwise get(final ResultSet rs,
                          final String[] names,
                          final SharedSessionContractImplementor session,
                          final Object owner) throws SQLException {

        final Integer value = rs.getInt(names[0]);
        return value != null ? Bitwise.of(value) : null;
    }

    @Override
    protected void set(final PreparedStatement st,
                       final Bitwise value,
                       final int index,
                       final SharedSessionContractImplementor session) throws SQLException {


        if (value == null) {
            st.setNull(index, Types.INTEGER);
        } else {
            st.setInt(index, value.getValue());
        }


    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.INTEGER};
    }
}
