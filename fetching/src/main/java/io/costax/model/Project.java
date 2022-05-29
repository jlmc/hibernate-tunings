package io.costax.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@NamedNativeQuery(
        name = "ProjectSummaryQuery",
        resultSetMapping = "ProjectSummaryMapper",
        query = "select p.id as id, p.title as title from project p order by p.id desc")

@SqlResultSetMapping(name = "ProjectSummaryMapper", classes = {
        @ConstructorResult(targetClass =  ProjectSummary.class,
                columns = {
                    @ColumnResult(name = "id", type = Long.class),
                    @ColumnResult(name = "title", type = String.class)
                }
        )
})

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
    private Set<Issue> issues = new HashSet<>();

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

    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        final Project project = (Project) o;
        return getId() != null && Objects.equals(getId(), project.getId());
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

    public Set<Issue> getIssues() {
        return Set.copyOf(this.issues);
    }

    public void add(Issue issue) {
        issue.setProject(this);
        this.issues.add(issue);
    }

    public void remove(Issue issue) {
        if (this.issues.contains(issue)) {
           issue.setProject(null);
           this.issues.remove(issue);
        }
    }
}
