package io.costax.persistence.api;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import java.util.function.BiFunction;

public class SnakeCasePhysicalNamingStrategy extends PhysicalNamingStrategyStandardImpl {

    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null)
            return null;

        String name = identifier.getText();
        String snakeName = name.replaceAll("([a-z]+)([A-Z]+)", "$1\\_$2").toLowerCase();

        if (!snakeName.equals(name)) {
            return new Identifier(snakeName, identifier.isQuoted());
        } else {
            return identifier;
        }
    }

    private Identifier toIdentifier(final BiFunction<Identifier, JdbcEnvironment, Identifier> mapper,
                                    final Identifier name,
                                    final JdbcEnvironment context) {
        return mapper.apply(toSnakeCase(name), context);
    }

    @Override
    public Identifier toPhysicalTableName(final Identifier name, final JdbcEnvironment context) {
        return toIdentifier(super::toPhysicalTableName, name, context);
    }

    @Override
    public Identifier toPhysicalColumnName(final Identifier name, final JdbcEnvironment context) {
        return toIdentifier(super::toPhysicalColumnName, name, context);
    }

    @Override
    public Identifier toPhysicalCatalogName(final Identifier name, final JdbcEnvironment context) {
        return toIdentifier(super::toPhysicalCatalogName, name, context);
    }

    @Override
    public Identifier toPhysicalSchemaName(final Identifier name, final JdbcEnvironment context) {
        return toIdentifier(super::toPhysicalSchemaName, name, context);
    }

    @Override
    public Identifier toPhysicalSequenceName(final Identifier name, final JdbcEnvironment context) {
        return toIdentifier(super::toPhysicalSequenceName, name, context);
    }
}
