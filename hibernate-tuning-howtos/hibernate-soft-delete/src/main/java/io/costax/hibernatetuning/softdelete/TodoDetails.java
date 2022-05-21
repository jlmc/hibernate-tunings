package io.costax.hibernatetuning.softdelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;

@Entity
@Table(name = "t_todo_details")
@SQLDelete(sql = "UPDATE t_todo_details SET deleted = true WHERE id = ?")
@Loader(namedQuery = "findTodoDetailsById")
@NamedQuery(name = "findTodoDetailsById",
        query = "select pd from TodoDetails pd where pd.deleted = false and pd.id = ?1")
@Where(clause = "deleted = false")
public class TodoDetails extends BaseEntity {
    @Id
    private Long id;
    @Column(name = "created_on")
    private OffsetDateTime createdOn;
    @Column(name = "created_by")
    private String createdBy;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @MapsId
    private Todo todo;

    protected TodoDetails() {
    }

    private TodoDetails(final OffsetDateTime createdOn, final String createdBy) {
        this.createdOn = createdOn;
        this.createdBy = createdBy;
    }

    public static TodoDetails of(final OffsetDateTime createdOn, final String createdBy) {
        return new TodoDetails(createdOn, createdBy);
    }

    /*
    public Long getId() {
        return id;
    }
    */

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setCreatedOn(final OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setDelete(boolean deleted) {
        super.deleted = deleted;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    protected void setTodo(final Todo todo) {
        this.todo = todo;
    }
}
