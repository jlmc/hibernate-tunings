package io.costax.relationships.manytomany;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tag")
public class Tag {

    @Id
    private Integer id;
    private String acronym;

    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();

    public static Tag of(final int id, final String acronym) {
        final Tag tag = new Tag();
        tag.id = id;
        tag.acronym = acronym;
        return tag;
    }

    protected void addPost(final Post post) {
        posts.add(post);
    }

    protected void removePost(final Post post) {
        posts.remove(post);
    }

    public Set<Post> getPosts() {
        return Set.copyOf(posts);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        final Tag tag = (Tag) o;
        return Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", acronym='" + acronym + '\'' +
                '}';
    }
}
