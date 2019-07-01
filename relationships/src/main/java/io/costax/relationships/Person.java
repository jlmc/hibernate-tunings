package io.costax.relationships;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Person {
    @Id
    private Integer id;
    private String name;

    /*
    @OneToOne(
            mappedBy = "person",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private PersonDetail detail;
    */


    public Person() {
    }

    /*
    public void setDetail(final PersonDetail detail) {
        if (detail == null) {
            if (this.detail != null) this.detail.setPerson(null);
        }
        else detail.setPerson(this);
        this.detail = detail;
    }
    */

    public Person(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        final Person person = (Person) o;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
