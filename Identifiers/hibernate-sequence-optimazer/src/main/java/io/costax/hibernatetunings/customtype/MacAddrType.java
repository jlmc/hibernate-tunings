package io.costax.hibernatetunings.customtype;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class MacAddrType extends ImmutableType<MacAddr> {

    public MacAddrType() {
        super(MacAddr.class);
    }


    @Override
    protected MacAddr get(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        final String mac = rs.getString(position);
        return mac != null ? MacAddr.of(mac) : null;
    }

    @Override
    protected void set(final PreparedStatement st,
                       final MacAddr value,
                       final int index,
                       final SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            PGobject holder = new PGobject();
            holder.setType("macaddr");
            holder.setValue(value.getAddress());
            st.setObject(index, holder);
        }
    }

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }
}
