package io.costax.relationships;

import javax.persistence.*;
import java.util.*;

@Entity
public class Movie {

    @Id
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "director_id", referencedColumnName = "id", nullable = false)
    private Director director;

    @OneToMany(
            mappedBy = "movie",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    Set<Review> reviews = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "movie_id", updatable = false, nullable = false)
    @OrderBy("li asc ")
    List<Scene> scenes = new ArrayList<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "entry")
    private List<MovieActorPersonage> actors = new ArrayList<>();

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

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setMovie(this);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
        review.setMovie(null);
    }

    public void addScene(Scene scene) {
        scenes.add(scene);
    }

    public void addReview(String comment) {
        addReview(Review.of(comment));
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

    public String getTitle() {
        return title;
    }

    public Set<Scene> getScenes() {
        return Set.copyOf(this.scenes);
    }


    public Integer getId() {
        return id;
    }

    public void addPersonage(Actor actor, String personageName) {
        final MovieActorPersonage movieActorPersonage = new MovieActorPersonage(this, actor, personageName);

        this.actors.add(movieActorPersonage);
    }

    public void removePersonage(Actor actor) {
        for (Iterator<MovieActorPersonage> iterator = actors.iterator(); iterator.hasNext(); ) {
            MovieActorPersonage movieActorPersonage = iterator.next();

            if (movieActorPersonage.getMovie().equals(this) && movieActorPersonage.getActor().equals(actor)) {

                iterator.remove();

                movieActorPersonage.getActor().getMovies().remove(movieActorPersonage);
                movieActorPersonage.setMovie(null);
                movieActorPersonage.setActor(null);
                //break;
            }
        }
    }
}
