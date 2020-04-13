package io.costax.hibernatetunings.customtype;


import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IPv4Type extends ImmutableType<IPv4> {

    public IPv4Type() {
        super(IPv4.class);
    }

    @Override
    protected IPv4 get(final ResultSet rs,
                       final String[] names,
                       final SharedSessionContractImplementor session,
                       final Object owner) throws SQLException {

        final String ip = rs.getString(names[0]);
        return ip != null ? new IPv4(ip) : null;
    }

    @Override
    protected void set(final PreparedStatement st,
                       final IPv4 value,
                       final int index,
                       final SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            PGobject holder = new PGobject();
            holder.setType("inet");
            holder.setValue(value.getAddress());
            st.setObject(index, holder);
        }
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.OTHER};
    }
}
