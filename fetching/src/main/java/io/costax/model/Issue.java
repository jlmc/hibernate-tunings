package io.costax.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "issue")
public class Issue extends BaseEntity {

    @ManyToOne //(cascade = {PERSIST, MERGE, REMOVE})
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    private String title;
    private String description;

    @CreationTimestamp
    @Column(name = "create_at")
    private OffsetDateTime createAt;

    protected Issue() {
    }

    private Issue(final Project project, final String title, final String description) {
        this.project = project;
        this.title = title;
        this.description = description;
    }

    public static Issue of(final Project project, final String title) {
        return new Issue(project, title, null);
    }

    public static Issue of(final Project project, final String title, String description) {
        return new Issue(project, title, description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Issue)) return false;
        final Issue issue = (Issue) o;
        return getId() != null && Objects.equals(getId(), issue.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void changeProject(final Project project, final String title) {
        this.project = project;
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreateAt() {
        return createAt;
    }

    protected void setProject(final Project project) {
        this.project = project;
    }
}
