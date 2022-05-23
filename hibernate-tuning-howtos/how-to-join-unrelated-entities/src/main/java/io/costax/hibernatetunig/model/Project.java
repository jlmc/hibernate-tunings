package io.costax.hibernatetunig.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

import java.util.Objects;

@Entity
public class Project {

    @Id
    private Long id;
    @Version
    private int version;
    private String title;

    public Project() {
    }

    private Project(final Long id, final String title) {
        this.id = id;
        this.title = title;
    }

    public static Project of(final Long id, final String title) {
        return new Project(id, title);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        final Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
