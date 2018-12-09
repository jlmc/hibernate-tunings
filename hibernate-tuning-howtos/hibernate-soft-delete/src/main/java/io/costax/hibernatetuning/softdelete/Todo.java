package io.costax.hibernatetuning.softdelete;

import org.hibernate.annotations.Loader;
import org.hibernate.annotations.ResultCheckStyle;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.*;

/**
 * The Todo entity being the root of our entity aggergate, it has relationships to TodoDetails, TodoComment, and Tag:
 */
@Entity(name = "Todo")
@Table(name = "todo", schema = "tasks")

// The @SqlDelete annotation allows you to override the default DELETE statement executed by Hibernate,
// so we substitute an UPDATE statement instead. Therefore, removing an entity will end up updating the deleted column to true.
@SQLDelete(sql = "UPDATE tasks.todo SET deleted = true WHERE id = ?", check = ResultCheckStyle.COUNT)

// The @Loader annotation allows us to customize the SELECT query used to load an entity by its identifier.
// Hence, we want to filter every record whose deleted column is set to true.
@Loader(namedQuery = "findTodoById")
@NamedQuery(name = "findTodoById", query = "select t from Todo t where t.deleted = false and t.id = ?1")

// The @Where clause is used for entity queries, and we want to provide it so that Hibernate can append the deleted column filtering condition to hide deleted rows.
// Prior to Hibernate 5.2, it was sufficient to provide the @Where clause annotation, in Hibernate 5.2, it’s important to provide a custom @Loader as well so that the direct fetching works as well.
@Where(clause = "deleted = false")
public class Todo extends BaseEntity {

    @Id
    private Long id;
    private String title;

    @OneToMany(
            mappedBy = "todo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<TodoComment> comments = new ArrayList<>();

    @OneToOne(
            mappedBy = "todo",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    //@LazyToOne(LazyToOneOption.NO_PROXY)
    private TodoDetails details;


    @ManyToMany
    @JoinTable(
            name = "todo_tag", schema = "tasks",
            joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

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
        comment.setPost(this);
    }

    public void removeComment(TodoComment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }

    public void addDetails(TodoDetails details) {
        this.details = details;
        details.setTodo(this);
    }

    public void removeDetails() {
        this.details.setTodo(null);
        this.details = null;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
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

    public TodoDetails getDetails() {
        return details;
    }

    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }
}
