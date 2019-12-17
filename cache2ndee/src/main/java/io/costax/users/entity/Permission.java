package io.costax.users.entity;

import io.costax.jsonb.JsonB;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@JsonB
@Entity
@Cacheable
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Permission {

    @Id
    @NotNull
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Deprecated
    public Permission() {
    }

    private Permission(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Permission of(final Integer id, final String name) {
        if (id == null) throw new IllegalArgumentException("the permission id should not be null");
        if (name == null) throw new IllegalArgumentException("the permission name should not be null");

        return new Permission(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        final Permission that = (Permission) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
