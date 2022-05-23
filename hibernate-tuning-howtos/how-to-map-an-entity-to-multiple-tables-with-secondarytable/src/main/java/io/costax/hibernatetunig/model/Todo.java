package io.costax.hibernatetunig.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity(name = "Todo")
@Table(
        name = "todo",
        schema = "tasks")
@SecondaryTables({
        @SecondaryTable(
                name = "todo_details",
                schema = "tasks",
                pkJoinColumns = {
                        @PrimaryKeyJoinColumn(
                                name = "id",
                                referencedColumnName = "id")
                }
        )
})
public class Todo {

    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private Long id;

    private String title;

    @Column(name = "created_on", table = "todo_details", nullable = false)
    private OffsetDateTime createdOn;

    @Column(name = "created_by", table = "todo_details", nullable = false)
    private String createdBy;

    public Todo() {
    }

    private Todo(final Long id, final String title, final OffsetDateTime createdOn, final String createdBy) {
        this.id = id;
        this.title = title;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public static Todo of(final Long id, final String title, final OffsetDateTime createdOn, final String createdBy) {
        return new Todo(id, title, createdOn, createdBy);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo)) return false;
        final Todo todo = (Todo) o;
        return Objects.equals(id, todo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
