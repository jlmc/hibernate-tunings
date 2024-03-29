package io.costax.hibernatetuning.softdelete;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "t_todo_comment")
@SQLDelete(sql = "UPDATE t_todo_comment SET deleted = true WHERE id = ?")
@Loader(namedQuery = "findTodoCommentById")
@NamedQuery(name = "findTodoCommentById", query =
        "select tc from TodoComment tc where tc.deleted = false and tc.id = ?1")
@Where(clause = "deleted = false")
public class TodoComment extends BaseEntity{

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false, updatable = false)
    private Todo todo;
    private String review;
    protected TodoComment() {
    }
    private TodoComment(final Long id, final String review) {
        this.id = id;
        this.review = review;
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
    public Todo getPost() {
        return todo;
    }
    public void setPost(final Todo todo) {
        this.todo = todo;
    }
}
