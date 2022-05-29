package io.costax.relationships.onetoone;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class PersonDetail {
    @Id
    private Integer id;

    private String local;
    private LocalDate bornAt;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "person_id", nullable = false, updatable = false)
    private Person person;

    public PersonDetail(final Person person, final String local, final LocalDate bornAt) {
        this.person = person;
        this.local = local;
        this.bornAt = bornAt;
        //this.id = id;
    }

    public PersonDetail() {
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDetail)) return false;
        final PersonDetail that = (PersonDetail) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public void setPerson(final Person person) {
        this.person = person;
    }
}
