package io.costax.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "article_blob")
public class ArticleBlob {

    @Id
    private Long id;
    private String title;

    //@org.hibernate.annotations.Nationalized
    //@Basic(fetch = FetchType.LAZY)
    @Lob
    private String content;

    @Lob
    private byte[] cover;


    protected ArticleBlob() {
    }

    private ArticleBlob(final Long id,
                        final String title,
                        final String content,
                        final byte[] cover) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.cover = cover;
    }

    public static ArticleBlob createArticleBlob(final Long id,
                                                final String title,
                                                final String content,
                                                final byte[] cover) {
        return new ArticleBlob(id, title, content, cover);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleBlob)) return false;
        final ArticleBlob that = (ArticleBlob) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ArticleBlob{" +
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

    public String getContent() {
        return content;
    }

    public byte[] getCover() {
        return cover;
    }
}
