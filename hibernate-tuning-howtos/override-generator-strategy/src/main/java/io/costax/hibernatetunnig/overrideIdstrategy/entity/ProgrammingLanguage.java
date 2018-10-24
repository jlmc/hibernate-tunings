package io.costax.hibernatetunnig.overrideIdstrategy.entity;

import io.costax.hibernatetunnig.overrideIdstrategy.generators.AssignedIdentityGenerator;
import io.costax.hibernatetunnig.overrideIdstrategy.generators.Identifiable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

@Entity
@Table(name = "programming_language")
public class ProgrammingLanguage implements Identifiable<Long> {

    @Id
    @GenericGenerator(name = "programming_language_identity", strategy = AssignedIdentityGenerator.STRATEGY)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "programming_language_identity")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "ProgrammingLanguage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
