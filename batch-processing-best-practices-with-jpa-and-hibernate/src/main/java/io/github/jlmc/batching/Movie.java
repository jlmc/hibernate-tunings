package io.github.jlmc.batching;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Movie {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "movie_generator")
    @SequenceGenerator(
            name="movie_generator",
            sequenceName = "movie_seq", initialValue = 1, allocationSize = 10)
    private Integer id;

    private String title;


    public Movie() { }


    private Movie(String title) {
        this.title = title;
    }

    public static Movie of(String title) {
        return new Movie(title);
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Movie movie = (Movie) o;
        return getId() != null && Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Movie {" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }
}
