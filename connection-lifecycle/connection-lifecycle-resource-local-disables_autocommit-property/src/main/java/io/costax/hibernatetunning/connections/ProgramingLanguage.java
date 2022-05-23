package io.costax.hibernatetunning.connections;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;

@Entity
@Table(name = "programing_language")
public class ProgramingLanguage implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true)
    private Long id;

    @NaturalId
    @Column(name = "name", nullable = false, updatable = false)
    private String name;

    protected ProgramingLanguage() {
    }

    private ProgramingLanguage(final String name) {
        this.name = name;
    }

    public static ProgramingLanguage of(final String name) {
        return new ProgramingLanguage(name);
    }

    public static ProgramingLanguage of(final Long id, final String name) {
        ProgramingLanguage of = of(name);
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
        return "io.costax.hibernatetunning.ProgramingLanguage{" +
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
        final ProgramingLanguage that = (ProgramingLanguage) o;
        return this.getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
