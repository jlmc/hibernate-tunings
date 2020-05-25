package io.costax.models;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Entity
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotBlank
    private String name;

    @NotNull
    @Column(name = "born_at", nullable = false, updatable = false, columnDefinition = "date not null")
    private LocalDate bornAt;

    @Generated(GenerationTime.INSERT)
    @Column(name = "stamp", insertable = false, updatable = false, columnDefinition = "timestamp not null default now()")
    private Instant stamp;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "athlete_sport",
            joinColumns = @JoinColumn(name = "Athlete_id", nullable = false, updatable = false),
            inverseJoinColumns = @JoinColumn(name = "sport_id", nullable = false, updatable = false)
    )
    @OrderColumn(name = "rank")
    private List<Sport> sports = new ArrayList<>();

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

    public void addSports(Collection<Sport> sports) {
        this.sports.addAll(sports);
    }

    public Set<Sport> getSports() {
        return Set.copyOf(this.sports);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", bornAt=" + bornAt +
                ", stamp=" + stamp +
                '}';
    }


}
