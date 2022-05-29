package io.costax.relationships.manytomany;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "developer")
public class Developer {

    @Id
    private Integer id;

    private String name;

    public Developer() {
    }

    private Developer(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Developer of(final Integer id, final String name) {
        return new Developer(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Developer)) return false;
        final Developer developer = (Developer) o;
        return Objects.equals(getId(), developer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Developer{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
