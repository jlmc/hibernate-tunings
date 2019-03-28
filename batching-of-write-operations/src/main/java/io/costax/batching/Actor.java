package io.costax.batching;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "actor", schema = "multimedia")
public class Actor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actor_generator")
    @SequenceGenerator(
            name = "actor_generator",
            sequenceName = "actor_many_seq",
            schema = "multimedia",
            initialValue = 100,
            allocationSize = 10)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ManyToMany(mappedBy = "actors", cascade = CascadeType.ALL)
    private Set<io.costax.batching.Serie> series = new HashSet<>();

    protected Actor() {
    }

    private Actor(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Actor of(final String firstName, final String lastName) {
        return new Actor(firstName, lastName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Actor)) return false;
        final Actor actor = (Actor) o;
        return this.id != null && Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public Set<Serie> getSeries() {
        return series;
    }
}
