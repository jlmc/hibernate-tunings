package io.costax.hibernatetunning.tasks;

import jakarta.persistence.*;

@Entity
@Table(schema = "tasks", name = "todo_comment")
public class TodoComment {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false, updatable = false)
    private Todo todo;

    private String review;

    @Basic(fetch = FetchType.LAZY)
    private byte[] attachment;

    protected TodoComment() {
    }

    private TodoComment(final Long id, final String review) {
        this.id = id;
        this.review = review;
    }

    public void setAttachment(final byte[] attachment) {
        this.attachment = attachment;
    }

    public static TodoComment of(final Long id, final String review) {
        return new TodoComment(id, review);
    }

    public Long getId() {
        return id;
    }

    public String getReview() {
        return review;
    }

    public Todo getTodo() {
        return todo;
    }

    public byte[] getAttachment() {
        return attachment;
    }

    public void setTodo(final Todo todo) {
        this.todo = todo;
    }
}
