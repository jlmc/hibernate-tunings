package io.costax.model;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Immutable
@Subselect(
        "SELECT " +
                "    functions.routine_name as name, " +
                "    string_agg(functions.data_type, ',') as params " +
                "FROM (" +
                "    SELECT " +
                "        routines.routine_name, " +
                "        parameters.data_type, " +
                "        parameters.ordinal_position " +
                "    FROM " +
                "        information_schema.routines " +
                "    LEFT JOIN " +
                "        information_schema.parameters " +
                "    ON " +
                "        routines.specific_name = parameters.specific_name " +
                "    WHERE " +
                "        routines.specific_schema='public' " +
                "    ORDER BY " +
                "        routines.routine_name, " +
                "        parameters.ordinal_position " +
                ") AS functions " +
                "GROUP BY functions.routine_name"
)
public class DatabaseFunction {

    @Id
    private String name;

    private String params;

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params.split(",");
    }

    public DatabaseFunction() {
    }
}
