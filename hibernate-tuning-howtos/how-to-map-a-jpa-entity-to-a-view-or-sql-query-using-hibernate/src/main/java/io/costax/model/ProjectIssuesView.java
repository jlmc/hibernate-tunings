package io.costax.model;

// select p.id as project_id, p.title as project_title, count(i.id) as number_of_issues

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "project_issues")
@Immutable
public class ProjectIssuesView {

    @Id
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_title")
    private String projectTitle;

    @Column(name = "number_of_issues")
    private Long numberOfIssues;

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public Long getNumberOfIssues() {
        return numberOfIssues;
    }
}
