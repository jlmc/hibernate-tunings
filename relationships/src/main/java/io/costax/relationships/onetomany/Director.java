package io.costax.relationships.onetomany;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Director {

    @Id
    private Integer id;
    private String name;

    //@OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    //List<Article> articles = new ArrayList<>();

    protected Director() {
    }

    private Director(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Director of(final Integer id, final String name) {
        return new Director(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Director)) return false;
        final Director author = (Director) o;
        return Objects.equals(id, author.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Director{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
