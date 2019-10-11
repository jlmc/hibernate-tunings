package io.costax.hibernatetunnig.overrideIdstrategy.entity;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "developer", uniqueConstraints = {
        @UniqueConstraint(name = "uk_developer_code", columnNames = {"licence_number"})
})
public class Developer {

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
    @JoinTable(name = "developer_programing_language",
            joinColumns = @JoinColumn(name = "developer_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<ProgramingLanguage> programingLanguages;
}
