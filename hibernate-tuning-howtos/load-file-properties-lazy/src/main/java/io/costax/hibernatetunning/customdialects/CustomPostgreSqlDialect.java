package io.costax.hibernatetunning.customdialects;

import java.sql.Types;

/**
 * An SQL dialect for Postgres 10.0 and later, adds support for PG-UUID data type in Native Queries.
 */
public class CustomPostgreSqlDialect extends org.hibernate.dialect.PostgreSQL95Dialect {

    public CustomPostgreSqlDialect() {
        super();
        
        registerHibernateType(Types.OTHER, "pg-uuid");

        // registe function to use in JPQL
        /*
        registerFunction("f_number_of_associated_risk_with_the_control", new org.hibernate.dialect.function.StandardSQLFunction("f_number_of_associated_risk_with_the_control", org.hibernate.type.StandardBasicTypes.LONG));
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
