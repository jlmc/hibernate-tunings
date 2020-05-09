package io.costax.models.hibernate;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "born_at", nullable = false, updatable = false)
    private LocalDate bornAt;

    @Generated(GenerationTime.INSERT)
    @Column(name = "stamp", insertable = false, updatable = false, columnDefinition = "timestamp not null default now()")
    private Instant stamp;

    @Generated(GenerationTime.INSERT)
    @Column(name = "rank", insertable = false, updatable = false, columnDefinition = "Integer not null default NEXTVAL('rank_id_seq')")
    private Long rank;

    //@formatter:off
    protected Athlete() {}
    //@formatter:on

    private Athlete(final String name, final LocalDate bornAt) {
        this.name = name;
        this.bornAt = bornAt;
    }

    public static Athlete of(final String name, final LocalDate bornAt) {
        return new Athlete(name, bornAt);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getBornAt() {
        return bornAt;
    }

    public Instant getStamp() {
        return stamp;
    }

    public Long getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bornAt=" + bornAt +
                ", stamp=" + stamp +
                ", rank=" + rank +
                '}';
    }
}
