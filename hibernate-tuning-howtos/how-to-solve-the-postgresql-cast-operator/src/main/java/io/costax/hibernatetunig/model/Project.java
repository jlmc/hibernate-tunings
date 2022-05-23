package io.costax.hibernatetunig.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    private String title;

    @OneToMany(
            mappedBy = "project",
            // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            orphanRemoval = true,
            //cascade = CascadeType.ALL
            cascade = {
                    CascadeType.PERSIST,
                    //CascadeType.REMOVE,
                    //CascadeType.DETACH,
                    CascadeType.MERGE
            })
    private List<Issue> issues = new ArrayList<>();

    protected Project() {
    }

    private Project(final String title) {
        this.title = title;
    }

    public static Project of(final String title) {
        return new Project(title);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Project project = (Project) o;
        return getId() != null && Objects.equals(getTitle(), project.getTitle());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Project{" +
                "title='" + title + '\'' +
                '}';
    }

    public List<Issue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    public void addIssue(Issue issue) {
        issue.setProject(this);
        this.issues.add(issue);
    }

    public void removeIssue(Issue issue) {
        issue.setProject(null);
        this.issues.remove(issue);
    }
}
