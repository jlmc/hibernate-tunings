package io.costax.hibernatetunings.customtype;


import com.vladmihalcea.hibernate.type.ImmutableType;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IPv4Type extends ImmutableType<IPv4> {
    protected IPv4Type() {
        super(IPv4.class);
    }

    @Override
    protected IPv4 get(ResultSet rs,
                       int position, SharedSessionContractImplementor sharedSessionContractImplementor,
                       Object o) throws SQLException {
        String ip = rs.getString(position);
        return ip != null ? new IPv4(ip) : null;
    }

    @Override
    protected void set(PreparedStatement ps,
                       IPv4 value,
                       int position,
                       SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
        if (value == null) {
            ps.setNull(position, Types.OTHER);
        } else {
            PGobject holder = new PGobject();
            holder.setType("inet");
            holder.setValue(value.getAddress());
            ps.setObject(position, holder);
        }
    }

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }
}
