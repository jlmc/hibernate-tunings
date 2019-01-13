package io.costax.hibernatetunig.customdialects;

import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;

/**
 * An SQL dialect for Postgres 10.0 and later, adds support for PG-UUID data type in Native Queries.
 */
public class CustomPostgreSqlDialect extends org.hibernate.dialect.PostgreSQL95Dialect {

    public CustomPostgreSqlDialect() {
        super();
        
        registerHibernateType(Types.OTHER, "pg-uuid");

        // registe function to use in JPQL
        // If we need the functions results in the queries projection then we must register the functions
        registerFunction("date", new org.hibernate.dialect.function.StandardSQLFunction("date", new LocalDateType()));
        registerFunction("date_part", new org.hibernate.dialect.function.StandardSQLFunction("date_part", StandardBasicTypes.INTEGER));

        /*
        registerFunction("f_document_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_document_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_control_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_control_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_control_system_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_control_system_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_control_periodicity_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_control_periodicity_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_control_nature_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_control_nature_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_process_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_process_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_risk_category_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_risk_category_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        registerFunction("f_impacted_objective_type_is_disable", new org.hibernate.dialect.function.StandardSQLFunction("f_impacted_objective_type_is_disable", org.hibernate.type.StandardBasicTypes.BOOLEAN));
        */
    }
}