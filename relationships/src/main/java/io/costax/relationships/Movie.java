package io.costax.relationships;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Movie {

    @Id
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "director_id", referencedColumnName = "id", nullable = false)
    private Director director;

    public Movie() {
    }

    private Movie(final Integer id, final String title, final Director director) {
        this.id = id;
        this.title = title;
        this.director = director;
    }

    public static Movie of(final Integer id, final String title, final Director director) {
        return new Movie(id, title, director);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        final Movie article = (Movie) o;
        return Objects.equals(id, article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public Director getDirector() {
        return director;
    }
}
