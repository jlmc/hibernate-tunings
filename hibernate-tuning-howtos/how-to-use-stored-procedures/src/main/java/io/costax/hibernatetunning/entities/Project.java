package io.costax.hibernatetunning.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureParameter;
import jakarta.persistence.Version;

@Entity
@NamedStoredProcedureQuery(
        name = "getProjects",
        procedureName = "get_projects",
        resultClasses = Project.class,
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.REF_CURSOR, type = void.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class)
        }
)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private int version;
    private String title;

    public Project() {
    }

    public Project(final String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }
}
