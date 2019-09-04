package io.costax.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.sql.Blob;
import java.sql.Clob;
import java.util.Objects;

@Entity
@Table(name = "article_clob")
public class ArticleClob {

    @Id
    private Long id;
    private String title;

    @Lob
    private java.sql.Clob content;

    @Lob
    private java.sql.Blob cover;


    protected ArticleClob() {
    }

    private ArticleClob(final Long id, final String title) {
        this.id = id;
        this.title = title;
    }

    public static ArticleClob createArticleClob(final Long id, final String title) {
        return new ArticleClob(id, title);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleClob)) return false;
        final ArticleClob that = (ArticleClob) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArticleClob{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Clob getContent() {
        return content;
    }

    public Blob getCover() {
        return cover;
    }

    public void setContent(final Clob content) {
        this.content = content;
    }

    public void setCover(final Blob cover) {
        this.cover = cover;
    }
}
