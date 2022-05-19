package io.costax.hibernatetunning.tasks;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(schema = "tasks", name = "todo")
@NamedNativeQuery(
        name = "TodoWithCommentByRank",
        query =
                "SELECT * " +
                        "FROM (   " +
                        "    SELECT *, dense_rank() OVER (ORDER BY \"td.title\", \"tdc.todo_id\") rank " +
                        "    FROM (   " +
                        "        SELECT td.id AS \"td.id\", " +
                        "               td.title AS \"td.title\", " +
                        // "               td.title AS \"td.title\", " +
                        "               tdc.id as \"tdc.id\", " +
                        "               tdc.review AS \"tdc.review\", " +
                        // "               td.review AS \"tdc.review\", " +
                        "               tdc.todo_id AS \"tdc.todo_id\" " +
                        //"              , tdc.attachment AS \"tdc.attachment\" " +
                        "        FROM tasks.todo td  " +
                        "        LEFT JOIN tasks.todo_comment tdc ON td.id = tdc.todo_id  " +
                        "        ORDER BY td.id " +
                        "    ) p_pc " +
                        ") p_pc_r " +
                        "WHERE p_pc_r.rank between :firstRecord and :lastRecord",
        resultSetMapping = "TodoWithCommentByRankMapping"
)
@SqlResultSetMapping(
        name = "TodoWithCommentByRankMapping",
        entities = {
                @EntityResult(
                        entityClass = Todo.class,
                        fields = {
                                @FieldResult(name = "id", column = "td.id"),
                                @FieldResult(name = "title", column = "td.title"),
                        }
                ),
                @EntityResult(
                        entityClass = TodoComment.class,
                        fields = {
                                @FieldResult(name = "id", column = "tdc.id"),
                                @FieldResult(name = "review", column = "tdc.review"),
                                @FieldResult(name = "todo", column = "tdc.todo_id"),
                        }
                )
        }
)
public class Todo {

    @Id
    private Long id;
    private String title;

    @OneToMany(
            mappedBy = "todo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TodoComment> comments = new ArrayList<>();

    protected Todo() {
    }

    private Todo(final Long id, final String title) {
        this.id = id;
        this.title = title;
    }

    public static Todo of(final Long id, final String title) {
        return new Todo(id, title);
    }

    public void addComment(TodoComment comment) {
        comments.add(comment);
        comment.setTodo(this);
    }

    public void removeComment(TodoComment comment) {
        comments.remove(comment);
        comment.setTodo(null);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<TodoComment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public void setComments(final ArrayList<TodoComment> objects) {
        this.comments = objects;
    }


}
