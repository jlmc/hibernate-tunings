package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public abstract class AbstractArrayType<T> implements UserType<T> {

    private final Class<T> arrayType;

    AbstractArrayType(Class<T> arrayType) {
        this.arrayType = arrayType;
    }

    public abstract T get(Array array) throws SQLException;

    abstract void set(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws SQLException;

    public int getSqlType() {
        return Types.ARRAY;
    }

    @Override
    public Class<T> returnedClass() {
        return arrayType;
    }

    @Override
    public T nullSafeGet(ResultSet rs,
                         int position,
                         SharedSessionContractImplementor session,
                         Object owner) throws SQLException {
        Array array = rs.getArray(position);
        if (array == null) {
            return null;
        }

        return get(array);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value != null && st != null) {

            set(st, value, index, session);

        } else if (st != null) {
            st.setNull(index, getSqlType());
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    public Serializable disassemble(T value) {
        return (Serializable) value;
    }

    @Override
    public T assemble(Serializable cached, Object owner) {
        return (T) cached;
    }

    @Override
    public T replace(T detached, T managed, Object owner) {
        return detached;
    }

    @Override
    public T deepCopy(T value) {
        if (value == null) return null;

        return ArraysUtils.deepCopy(value);
    }

    @Override
    public boolean equals(T x, T y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(T x) {
        return Objects.hashCode(x);
    }
}
