package io.costax.batching.modek;

import io.costax.batching.Actor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "serie", schema = "multimedia")
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serie_generator")
    @SequenceGenerator(
            name = "serie_generator",
            schema = "multimedia",
            sequenceName = "serie_many_seq",
            allocationSize = 5,
            initialValue = 100)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column
    private String title;

    @Column
    private String description;

    @ManyToMany
    @JoinTable(
            name = "serie_actor",
            schema = "multimedia",
            joinColumns = {@JoinColumn(name = "serie_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "actor_id", referencedColumnName = "id")})
    private Set<Actor> actors = new HashSet<>();

    public Serie() {
    }

    private Serie(final String title, final String description) {
        this.title = title;
        this.description = description;
    }

    public static Serie of(final String title, final String description) {
        return new Serie(title, description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Serie)) return false;
        final Serie serie = (Serie) o;
        return this.id != null && Objects.equals(id, serie.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void addActor(final Actor actor) {
        this.actors.add(actor);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
