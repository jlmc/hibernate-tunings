package io.costax.hibernatetunings.customtype;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public abstract class ImmutableType<T> implements UserType<T> {

    private final Class<T> clazz;

    protected ImmutableType(final Class<T> clazz) {
        this.clazz = clazz;
    }

    protected abstract T get(ResultSet rs, int position,
                             SharedSessionContractImplementor session,
                             Object owner) throws SQLException;

    protected abstract void set(PreparedStatement st, T value, int index,
                                SharedSessionContractImplementor session) throws SQLException;

    @Override
    public T nullSafeGet(ResultSet rs,
                         int position,
                         SharedSessionContractImplementor session,
                         Object owner) throws SQLException {
        return get(rs, position, session, owner);
    }

    @Override
    public void nullSafeSet(PreparedStatement st,
                            T value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        set(st, clazz.cast(value), index, session);
    }

    @Override
    public Class<T> returnedClass() {
        return clazz;
    }

    @Override
    public boolean equals(T x, T y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(T x) {
        return x.hashCode();
    }

    @Override
    public T deepCopy(T value) {
        return (T) value;
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
    public T replace(T o, T target, Object owner) {
        return o;
    }
}
