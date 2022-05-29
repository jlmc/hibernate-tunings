package io.github.jlmc.types.usetypes;

import io.github.jlmc.types.Inet;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class InetType extends ImmutableType<Inet> {

    @Override
    protected Inet get(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String string = rs.getString(position);
        return string != null ? new Inet(string) : null;
    }

    @Override
    protected void set(PreparedStatement st, Inet value, int index, SharedSessionContractImplementor session) throws SQLException {
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
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<Inet> returnedClass() {
        return Inet.class;
    }
}
