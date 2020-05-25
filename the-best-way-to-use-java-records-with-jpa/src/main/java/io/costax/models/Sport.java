package io.costax.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Sport {

    @Id
    @NotNull
    private Integer id;
    @NotBlank
    private String name;

    public Sport() {
    }

    private Sport(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Sport of(final Integer id, final String name) {
        return new Sport(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Sport sport = (Sport) o;
        return Objects.equals(id, sport.id);
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

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
