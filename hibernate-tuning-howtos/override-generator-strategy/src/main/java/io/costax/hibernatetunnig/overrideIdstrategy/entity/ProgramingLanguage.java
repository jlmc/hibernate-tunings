package io.costax.hibernatetunnig.overrideIdstrategy.entity;

import io.costax.hibernatetunnig.overrideIdstrategy.generators.AssignedIdentityGenerator;
import io.costax.hibernatetunnig.overrideIdstrategy.generators.Identifiable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;

@Entity
@Table(name = "programing_language")
public class ProgramingLanguage implements Identifiable<Long> {

    @Id
    @GenericGenerator(name = "programing_language_identity", strategy = AssignedIdentityGenerator.STRATEGY)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "programing_language_identity")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
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
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ProgramingLanguage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
