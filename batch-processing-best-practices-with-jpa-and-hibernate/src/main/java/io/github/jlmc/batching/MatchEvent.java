package io.github.jlmc.batching;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "match_events")
@SequenceGenerator(
        name="match_events_generator",
        sequenceName = "match_events_seq", initialValue = 100, allocationSize = 20)
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "match_events_generator")
    @Column(name = "id", unique = true, updatable = false)
    private Integer id;

    private Integer minute;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false, updatable = false)
    private Match match;


    //@formatter:off
    public MatchEvent() { }
    //@formatter:on

    private MatchEvent(Integer minute, String description) {
        this.minute = minute;
        this.description = description;
    }

    public static MatchEvent of(Integer minute, String description) {
        return new MatchEvent(minute, description);
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Integer getMinute() {
        return minute;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MatchEvent that = (MatchEvent) o;
        return getId() != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public void setMatch(final Match match) {
        this.match = match;
    }
}
