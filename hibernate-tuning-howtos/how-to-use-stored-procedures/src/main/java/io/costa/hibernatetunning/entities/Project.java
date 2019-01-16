package io.costa.hibernatetunning.entities;

import javax.persistence.*;

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
