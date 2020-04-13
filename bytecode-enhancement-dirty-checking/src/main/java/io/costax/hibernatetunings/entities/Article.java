package io.costax.hibernatetunings.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Article {

    @Id
    private Integer id;
    private String name;

    public Article() {}

    private Article(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Article of(final Integer id, final String name) {
        return new Article(id, name);
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Article article = (Article) o;
        return Objects.equals(getId(), article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
