package io.costa.hibernatetunings.entities.exchange;

import javax.persistence.*;

@Entity
@Table(schema = "exchange", name = "tread")
public class Tread {

    @Id
    @GeneratedValue(generator = "tread_sec_generator")
    @SequenceGenerator(name = "tread_sec_generator",
            sequenceName = "tread_sec",
            schema = "exchange",
            allocationSize = 25)
    private Long id;

    private short value;

    protected Tread() {
    }

    public Tread(final short value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public short getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Tread tread = (Tread) o;
        return getId() != null && id.equals(tread.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Tread{" +
                "id=" + id +
                ", value=" + value +
                '}';
    }
}
