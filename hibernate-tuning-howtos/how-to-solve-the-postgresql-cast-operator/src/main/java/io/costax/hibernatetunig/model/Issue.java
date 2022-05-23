package io.costax.hibernatetunig.model;

import io.costax.hibernatetunig.model.projections.IssueSummary;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "issue")
@SqlResultSetMapping(
        name = "IssueSummaryMapper",
        classes = @ConstructorResult(
                targetClass = IssueSummary.class,
                columns = {
                        @ColumnResult(name = "id", type = Long.class),
                        @ColumnResult(name = "title", type = String.class),
                        @ColumnResult(name = "day", type = LocalDate.class),
                }
        )
)
public class Issue extends BaseEntity {

    @ManyToOne //(cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    private String title;
    private String description;

    //@CreationTimestamp
    @Column(name = "create_at")
    private OffsetDateTime createAt;

    public Issue() {
    }

    private Issue(final Project project, final String title, final OffsetDateTime createAt) {
        this.project = project;
        this.title = title;
        this.createAt = createAt;
    }

    public static Issue of(final Project project, final String title, final OffsetDateTime createAt) {
        return new Issue(project, title, createAt);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Issue issue = (Issue) o;
        return getId() != null && Objects.equals(createAt, issue.createAt);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void changeProject(final Project project, final String title) {
        this.project = project;
        this.title = title;

    }

    protected void setProject(final Project project) {
        this.project = project;
    }
}
