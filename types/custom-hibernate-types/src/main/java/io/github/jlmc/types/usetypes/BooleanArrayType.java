package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BooleanArrayType extends AbstractArrayType<boolean[]> {


    public BooleanArrayType() {
        super(boolean[].class);
    }

    @Override
    public boolean[] get(Array array) throws SQLException {
        return ArraysUtils.asBooleanArray(array.getArray());
    }

    @Override
    void set(PreparedStatement st, boolean[] value, int index, SharedSessionContractImplementor session)throws SQLException {
        st.setArray(index,
                st.getConnection()
                  .createArrayOf("boolean", ArraysUtils.asObjectArray(value)));
    }
}
