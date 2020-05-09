package io.costax.models.specification;

import javax.persistence.*;
import java.time.Instant;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "occurrence")
public class Occurrence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "occurrence_seq")
    @SequenceGenerator(name = "occurrence_seq", sequenceName = "occurrence_seq", allocationSize = 3)
    private Integer id;

    private String description;

    @OneToOne(optional = true,
            //orphanRemoval = true, // if you want to remove also the records of the EventIdentifier when you delete one Occurrence, The CascadeType.REMOVE will have the same effect
            cascade = {MERGE, PERSIST, DETACH})
    @JoinColumn(name = "event_id")
    private EventIdentifier eventIdentifier;

    @Column(name = "stamp", insertable = false, updatable = false, columnDefinition = "timestamp not null default now()")
    private Instant stamp;

    //@formatter:off
    protected Occurrence() {}
    //@formatter:on

    private Occurrence(final String description, final EventIdentifier eventIdentifier) {
        this.description = description;
        this.eventIdentifier = eventIdentifier;
    }

    public static Occurrence of(final String description) {
        return new Occurrence(description, new EventIdentifier());
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public EventIdentifier getEventIdentifier() {
        return eventIdentifier;
    }

    public Instant getStamp() {
        return stamp;
    }

    @Override
    public String toString() {
        return "Occurrence{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", eventIdentifier=" + eventIdentifier +
                ", stamp=" + stamp +
                '}';
    }
}
