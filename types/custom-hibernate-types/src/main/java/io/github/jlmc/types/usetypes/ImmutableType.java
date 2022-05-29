package io.github.jlmc.types.usetypes;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class ImmutableType<T> implements UserType<T> {

    protected abstract T get(final ResultSet rs,
                             final int position,
                             final SharedSessionContractImplementor session,
                             final Object owner) throws SQLException;

    protected abstract void set(PreparedStatement st,
                                T value,
                                int index,
                                SharedSessionContractImplementor session) throws SQLException;


    @Override
    public T nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return get(rs, position, session, owner);
    }


    @Override
    public void nullSafeSet(final PreparedStatement st,
                            final T value,
                            final int index,
                            final SharedSessionContractImplementor session) throws SQLException {
        set(st, returnedClass().cast(value), index, session);
    }

    @Override
    public boolean equals(T x, T y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(T x) {
        return Objects.hashCode(x);
    }

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T o) {
        return (Serializable) o;
    }

    @Override
    public T assemble(Serializable cached, Object owner) {
        return (T) cached;
    }

    @Override
    public T replace(T detached, T managed, Object owner) {
        return detached;
    }
}
