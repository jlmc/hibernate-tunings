package io.costax.hibernatetuning.softdelete;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Objects;

@Entity(name = "Tag")
@Table(name = "t_tag")
@SQLDelete(
        sql = "UPDATE t_tag SET deleted = true WHERE id = ?")
@Loader(namedQuery = "findTagById")
@NamedQuery(
        name = "findTagById",
        query = "select t from Tag t where t.deleted = false and t.id = ?1"
)
@Where(clause = "deleted = false")
public class Tag extends BaseEntity {

    @Id
    private Long id;

    private String name;

    protected Tag() {
    }

    private Tag(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Tag of(final Long id, final String name) {
        return new Tag(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tag tag = (Tag) o;
        return id != null && Objects.equals(id, tag.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
