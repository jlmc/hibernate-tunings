package io.costax.relationships;

import javax.persistence.*;
import java.util.*;

@Entity
public class Actor {

    @Id
    private Integer id;

    private String name;

    @ElementCollection(
            fetch = FetchType.LAZY)
    @JoinTable(
            name = "actor_language",
            joinColumns = { @JoinColumn(name = "actor_id", nullable = false) })
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Set<Language> languages = new HashSet<>();


    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "actor_prize",
            joinColumns = @JoinColumn(name = "actor_id", nullable = false, updatable = false))
    @AttributeOverrides({
            @AttributeOverride(name = "at", column = @Column(name = "recived_at", nullable = false, updatable = false)),
            @AttributeOverride(name = "value", column = @Column(name = "prize_value", nullable = false, updatable = false))
    })
    private Set<Prize> prizes = new HashSet<>();

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieActorPersonage> movies = new ArrayList<>();

    protected Actor() {
    }

    private Actor(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Actor of(final Integer id, final String name) {
        return new Actor(id, name);
    }

    public Set<Language> getLanguages() {
        return languages;
    }

    public void addLanguage(Language language) {
        this.languages.add(language);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor)) return false;
        final Actor actor = (Actor) o;
        return Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void removeLanguage(Language language) {
        languages.remove(language);
    }

    public void addPrize(Prize prize) {
        this.prizes.add(prize);
    }

    public void removePrize(Prize prize) {
        this.prizes.remove(prize);
    }


    public Integer getId() {
        return id;
    }

    protected List<MovieActorPersonage> getMovies() {
        return movies;
    }
}
