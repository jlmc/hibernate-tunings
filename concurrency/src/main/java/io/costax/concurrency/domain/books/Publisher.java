package io.costax.concurrency.domain.books;

import org.hibernate.annotations.Type;
import org.hibernate.type.TimestampType;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(schema = "multimedia", name = "Publisher")
public class Publisher {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private Integer id;

    /**
     * DbTimestampType
     *
     * <tt>dbtimestamp</tt>: An extension of {@link TimestampType} which
     * maps to the database's current timestamp, rather than the jvm's
     * current timestamp.
     * <p/>
     * Note: May/may-not cause issues on dialects which do not properly support
     * a true notion of timestamp (Oracle < 8, for example, where only its DATE
     * datatype is supported).  Depends on the frequency of DML operations...
     */
    @Version
    @Type(type = "dbtimestamp")
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
