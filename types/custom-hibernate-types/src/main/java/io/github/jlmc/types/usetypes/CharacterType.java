package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CharacterType extends ImmutableType<Character> {

    @Override
    protected Character get(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        String value = rs.getString(position);
        return (value != null && value.length() > 0) ? value.charAt(0) : null;
    }

    @Override
    protected void set(PreparedStatement st, Character value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.CHAR);
        } else {
            st.setString(index, String.valueOf(value));
        }
    }

    @Override
    public int getSqlType() {
        return Types.CHAR;
    }

    @Override
    public Class<Character> returnedClass() {
        return Character.class;
    }
}
