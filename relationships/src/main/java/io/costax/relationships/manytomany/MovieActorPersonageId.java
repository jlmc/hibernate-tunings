package io.costax.relationships.manytomany;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class MovieActorPersonageId implements Serializable {

    private Integer movieId;
    private Integer actorId;

    protected MovieActorPersonageId() {
    }

    private MovieActorPersonageId(final Integer movieId, final Integer actorId) {
        this.movieId = movieId;
        this.actorId = actorId;
    }

    public static MovieActorPersonageId of(final Integer movieId, final Integer actorId) {
        return new MovieActorPersonageId(movieId, actorId);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MovieActorPersonageId)) return false;
        final MovieActorPersonageId that = (MovieActorPersonageId) o;
        return Objects.equals(movieId, that.movieId) &&
                Objects.equals(actorId, that.actorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, actorId);
    }
}
