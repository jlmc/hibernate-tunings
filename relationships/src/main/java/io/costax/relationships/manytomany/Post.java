package io.costax.relationships.manytomany;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "post")
public class Post {

    @Id
    private Integer id;
    private String title;

    /**
     * For `@ManyToMany` associations, {@link CascadeType#REMOVE} does not make too much sense when both sides represent independent entities.
     * In this case, removing a {@code Post } entity should not trigger a Script removal because the {@code Tag} can be referenced by other {@code Post } as well.
     * The same arguments apply to orphan removal since removing an entry from the tags collection should only delete the junction record and not the target {@code Tag} entity.
     * For both unidirectional and bidirectional associations, it is better to avoid the {@link CascadeType#REMOVE} mapping.
     * Instead of {@code @ManyToMany(cascade = CascadeType.ALL )}, the cascade attributes should be declared explicitly
     * (e.g. {@code @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )})
     * <p>
     * <p>
     * Use a List it is not a good idea,
     * because if it is necessary to remove one single reference from the collection:
     * 1.DELETE statement will be trigger : {@code delete from Post_Tag where Post_id = :id}
     * 2.Many INSERT statements will be trigger with records that already have been inserted before.
     * A Set collection have a much better performance but as consequence the @OrderColumn(name = "li")
     * don't work.
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false, updatable = false)
    )
    private Set<Tag> tags = new HashSet<>();


    public Post() {
    }

    private Post(final Integer id, final String title) {
        this.id = id;
        this.title = title;
    }

    public static Post of(final int i, final String s) {
        return new Post(i, s);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        final Post post = (Post) o;
        return Objects.equals(getId(), post.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Set<Tag> getTags() {
        return Set.copyOf(tags);
    }


    public void addTag(Tag tag) {
        tags.add(tag);
        tag.addPost(this);
    }

    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.removePost(this);
    }

}
