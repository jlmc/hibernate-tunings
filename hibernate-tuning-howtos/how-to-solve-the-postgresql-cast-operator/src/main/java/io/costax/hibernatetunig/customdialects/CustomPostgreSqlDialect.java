package io.costax.hibernatetunig.customdialects;

import org.hibernate.type.LocalDateType;
import org.hibernate.type.StandardBasicTypes;

import java.sql.Types;
import java.util.List;

/**
 * An SQL dialect for Postgres 10.0 and later, adds support for PG-UUID data type in Native Queries.
 */
public class CustomPostgreSqlDialect extends org.hibernate.dialect.PostgreSQL10Dialect {

    public CustomPostgreSqlDialect() {
        super();
        
        registerHibernateType(Types.OTHER, "pg-uuid");

        // register function to use in JPQL
        // If we need the functions results in the queries projection then we must register the functions
        registerFunction("date", new org.hibernate.dialect.function.StandardSQLFunction("date", new LocalDateType()));
        registerFunction("date_part", new org.hibernate.dialect.function.StandardSQLFunction("date_part", StandardBasicTypes.INTEGER));
        registerFunction("replace", new org.hibernate.dialect.function.StandardSQLFunction("replace", StandardBasicTypes.STRING));

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

    /**
     * An SQL Dialect for PostgreSQL 10 and later. Adds support for Partition table.
     *
     * @param tableTypesList
     */
    @Override
    public void augmentRecognizedTableTypes(List<String> tableTypesList) {
        super.augmentRecognizedTableTypes( tableTypesList );
        tableTypesList.add( "PARTITIONED TABLE" );
    }
}
