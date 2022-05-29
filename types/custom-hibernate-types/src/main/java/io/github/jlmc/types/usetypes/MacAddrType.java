package io.github.jlmc.types.usetypes;

import io.github.jlmc.types.MacAddr;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class MacAddrType extends ImmutableType<MacAddr> {

    @Override
    protected MacAddr get(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        final String mac = rs.getString(position);
        return mac != null ? MacAddr.of(mac) : null;
    }

    @Override
    protected void set(PreparedStatement st, MacAddr value, int index, SharedSessionContractImplementor session) throws SQLException {
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

    @Override
    public Class<MacAddr> returnedClass() {
        return MacAddr.class;
    }
}
