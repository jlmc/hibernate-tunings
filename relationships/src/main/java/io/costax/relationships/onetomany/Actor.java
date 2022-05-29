package io.costax.relationships.onetomany;

import io.costax.relationships.elementcollections.Language;
import io.costax.relationships.elementcollections.Prize;
import io.costax.relationships.manytomany.MovieActorPersonage;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Actor {

    @Id
    private Integer id;

    private String name;

    @ElementCollection(
            fetch = FetchType.LAZY)
    @JoinTable(
            name = "actor_language",
            joinColumns = {@JoinColumn(name = "actor_id", nullable = false)})
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

    @OneToMany(
            mappedBy = "actor",
            // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant,
            // in this case we still use it just because we want all the others CascadeTypes.
            cascade = CascadeType.ALL,
            orphanRemoval = true)
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

    public List<MovieActorPersonage> getMovies() {
        return movies;
    }
}
