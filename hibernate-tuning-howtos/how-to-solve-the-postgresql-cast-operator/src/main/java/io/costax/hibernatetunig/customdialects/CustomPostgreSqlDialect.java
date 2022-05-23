package io.costax.hibernatetunig.customdialects;

import org.hibernate.query.spi.QueryEngine;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

/**
 * An SQL dialect for Postgres 10.0 and later, adds support for PG-UUID data type in Native Queries.
 */
public class CustomPostgreSqlDialect extends org.hibernate.dialect.PostgreSQLDialect {

    public CustomPostgreSqlDialect() {
        super();
        //registerHibernateType(Types.OTHER, "pg-uuid");
        // register function to use in JPQL
        // If we need the functions results in the queries projection then we must register the functions
        //registerFunction("date", new org.hibernate.dialect.function.StandardSQLFunction("date", new LocalDateType()));
        //registerFunction("date_part", new org.hibernate.dialect.function.StandardSQLFunction("date_part", StandardBasicTypes.INTEGER));
        //registerFunction("replace", new org.hibernate.dialect.function.StandardSQLFunction("replace", StandardBasicTypes.STRING));
    }

    @Override
    public void initializeFunctionRegistry(QueryEngine queryEngine) {
        BasicTypeRegistry basicTypeRegistry = queryEngine.getTypeConfiguration().getBasicTypeRegistry();

        SqmFunctionRegistry functionRegistry = queryEngine.getSqmFunctionRegistry();
        functionRegistry.registerNamed("date",
                basicTypeRegistry.resolve(StandardBasicTypes.LOCAL_DATE));
        functionRegistry.registerNamed("date_part",
                basicTypeRegistry.resolve(StandardBasicTypes.INTEGER));
        functionRegistry.registerNamed("replace",
                basicTypeRegistry.resolve(StandardBasicTypes.STRING));

        super.initializeFunctionRegistry(queryEngine);

        //registerHibernateType(Types.OTHER, "pg-uuid");
    }
}
