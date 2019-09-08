package io.costax.relationships.onetomany;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Review {

    @Id
    @GeneratedValue
    private Integer id;

    private String comment;

    @ManyToOne
    @JoinColumn(
            name = "movie_id",
            updatable = false,
            nullable = false
    )
    private Movie movie;

    protected Review() {}

    public static Review of (final String comment) {
        Review review = new Review();
        review.comment = comment;
        return review;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        final Review review = (Review) o;
        return this.id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    void setMovie(final Movie movie) {
        this.movie = movie;
    }
}
