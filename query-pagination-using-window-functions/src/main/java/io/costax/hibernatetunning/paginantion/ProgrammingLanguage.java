package io.costax.hibernatetunning.paginantion;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "programming_language")
public class ProgrammingLanguage implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    private Long id;

    @NaturalId
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    protected ProgrammingLanguage() {
    }

    private ProgrammingLanguage(final String name) {
        this.name = name;
    }

    public static ProgrammingLanguage of(final String name) {
        return new ProgrammingLanguage(name);
    }

    public static ProgrammingLanguage of(final Long id, final String name) {
        ProgrammingLanguage of = of(name);
        of.id = id;
        return of;
    }

    @PreUpdate
    @PrePersist
    private void nameToLowerCase() {
        this.name = name.toLowerCase();
    }

    @Override
    public String toString() {
        return "io.costax.hibernatetunning.paginantion.ProgrammingLanguage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ProgrammingLanguage that = (ProgrammingLanguage) o;
        return this.getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
