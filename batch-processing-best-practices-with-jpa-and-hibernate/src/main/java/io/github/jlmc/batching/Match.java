package io.github.jlmc.batching;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "matches")
@SequenceGenerator(
        name="matches_generator",
        sequenceName = "matches_seq", initialValue = 10, allocationSize = 10)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "matches_generator")
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    private LocalDate at;

    private String homeTeam;

    private String awayTeam;

    @OneToMany(
            mappedBy = "match",
            orphanRemoval = true, // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            cascade = CascadeType.ALL
            //cascade = {CascadeType.PERSIST, CascadeType.MERGE}
            )
    private Set<MatchEvent> events = new HashSet<>();

    @Version
    private int version;

    //@formatter:off
    public Match() { }
    //@formatter:on

    private Match(final Integer id,
                  final LocalDate at,
                  final String homeTeam,
                  final String awayTeam) {
        this.id = id;
        this.at = at;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
    }

    public static Match of(final LocalDate at,
                           final String homeTeam,
                           final String awayTeam,
                           final Integer id) {
        return new Match(id, at, homeTeam, awayTeam);
    }

    public static Match of(final LocalDate at,
                           final String homeTeam,
                           final String awayTeam) {
        return of(at, homeTeam, awayTeam, null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Match match = (Match) o;
        return getId() != null && Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getAt() {
        return at;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setHomeTeam(final String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(final String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Set<MatchEvent> getEvents() {
        return events;
    }

    public Match addEvent(MatchEvent event) {
        this.events.add(event);
        event.setMatch(this);
        return this;
    }
}
