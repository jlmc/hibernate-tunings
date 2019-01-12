package io.costax.hibernatetunig.model.projections;

import java.time.LocalDate;

public class IssueSummary {
    private final Long id;
    private final String title;
    private final LocalDate day;

    public IssueSummary(final Long id, final String title, final LocalDate day) {
        this.id = id;
        this.title = title;
        this.day = day;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDay() {
        return day;
    }

    @Override
    public String toString() {
        return "IssueSummary{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", day=" + day +
                '}';
    }
}
