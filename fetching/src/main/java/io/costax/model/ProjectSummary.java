package io.costax.model;

import java.io.Serializable;

public class ProjectSummary implements Serializable {

    private Long id;
    private String title;

    public ProjectSummary() {
    }

    public ProjectSummary(final Long id, final String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "ProjectSummary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    protected void setId(final Number id) {
        this.id = id.longValue();
    }

    protected void setTitle(final String title) {
        this.title = title;
    }
}
