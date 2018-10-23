package io.costax.hibernatetunning.connections;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "developer", uniqueConstraints = {
        @UniqueConstraint(name = "uk_developer_code", columnNames = {"licence_number"})
})
public class Developer implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    private String name;

    @NaturalId
    //@NotNull
    @Column(name = "licence_number", unique = true)
    private String licenceNumber;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "developer_programmig_language",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<ProgrammingLanguage> programmingLanguages = new HashSet<>();


    protected Developer() {
    }

    private Developer(final String name, final String licenceNumber) {
        this.name = name;
        this.licenceNumber = licenceNumber;
    }

    public static Developer of(final String name, final String licenceNumber) {
        return new Developer(name, licenceNumber);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Developer developer = (Developer) o;
        return Objects.equals(getId(), developer.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Developer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenceNumber='" + licenceNumber + '\'' +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public Set<ProgrammingLanguage> getProgrammingLanguages() {
        return Collections.unmodifiableSet(programmingLanguages);
    }

    public void add(ProgrammingLanguage pl) {
        this.programmingLanguages.add(pl);
    }
}
