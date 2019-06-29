package io.costax.relationships;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "movie_actor_personage")
public class MovieActorPersonage {

    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "movieId", column = @Column(name = "movie_id", nullable = false, updatable = false)),
            @AttributeOverride(name = "actorId", column = @Column(name = "actorId", nullable = false, updatable = false))
    })
    private MovieActorPersonageId id;

    @ManyToOne
    @MapsId("movieId")
    private Movie movie;

    @ManyToOne
    @MapsId("actorId")
    private Actor actor;

    private String personage;

    protected MovieActorPersonage() {}

    public MovieActorPersonage(Movie movie, Actor actor, String personage) {
        this.movie = movie;
        this.actor = actor;
        this.id = MovieActorPersonageId.of(movie.getId(), actor.getId());

        this.personage = personage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieActorPersonage that = (MovieActorPersonage) o;
        return Objects.equals(movie, that.movie) &&
                Objects.equals(actor, that.actor);
    }
    @Override
    public int hashCode() {
        return Objects.hash(movie, actor);
    }


    public Movie getMovie() {
        return movie;
    }

    public Actor getActor() {
        return actor;
    }

    protected void setMovie(final Movie movie) {
        this.movie = movie;
    }

    protected void setActor(final Actor actor) {
        this.actor = actor;
    }
}
