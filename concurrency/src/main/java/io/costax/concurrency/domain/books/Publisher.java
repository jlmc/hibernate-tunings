package io.costax.concurrency.domain.books;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(schema = "multimedia", name = "Publisher")
public class Publisher {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    @Version
    @Column(name = "mod_date")
    private Date modDate;

    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Publisher)) return false;
        final Publisher publisher = (Publisher) o;
        return Objects.equals(id, publisher.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "id=" + id +
                ", modDate=" + modDate +
                ", name='" + name + '\'' +
                '}';
    }
}
