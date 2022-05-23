package io.costax.hibernatetunig.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Article {

    @Id
    private Long id;
    private String name;

    public Article() {
    }

    private Article(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Article of(final Long id, final String name) {
        return new Article(id, name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        final Article article = (Article) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
