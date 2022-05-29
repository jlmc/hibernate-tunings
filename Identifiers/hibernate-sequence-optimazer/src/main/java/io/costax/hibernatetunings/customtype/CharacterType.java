package io.costax.hibernatetunings.customtype;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class CharacterType extends ImmutableType<Character> {

    public CharacterType() {
        super(Character.class);
    }

    @Override
    protected Character get(ResultSet rs,
                            int position,
                            SharedSessionContractImplementor session,
                            Object owner) throws SQLException {
        String value = rs.getString(position);
        return (value != null && value.length() > 0) ? value.charAt(0) : null;
    }

    @Override
    public void set(PreparedStatement st, Character value, int index,
                    SharedSessionContractImplementor session) throws SQLException {
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
}
