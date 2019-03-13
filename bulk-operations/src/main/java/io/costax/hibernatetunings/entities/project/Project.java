package io.costax.hibernatetunings.entities.project;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


//@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Entity
@Table(name = "project")
public class Project extends BaseEntity {

    private String title;

    /**
     * this is a specific hibernate feature, that allows us to cache the collection
     */
    //@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(
            mappedBy = "project",
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
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

    public void setTitle(final String title) {
        this.title = title;
    }
}
