package io.costax.model;

import javax.persistence.*;
import java.util.Objects;


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
}
