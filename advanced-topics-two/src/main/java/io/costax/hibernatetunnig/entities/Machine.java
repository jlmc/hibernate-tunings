package io.costax.hibernatetunnig.entities;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.Objects;

/**
 * {@link NotFound} in hibernate is used when entities are associated to each other by ManyToOne, OneToMany etc. Suppose joined subclass has no data related to any id due to some database inconsistency. And we do not want to throw error, in this case @NotFound helps us. If we use @NotFound, then for any id if there is no data in associated joined subclass, error will not be thrown.
 * {@link NotFound} has two action {@link NotFoundAction#IGNORE} and {@link NotFoundAction#EXCEPTION}.
 */
@Entity
public class Machine {

    @Id
    private Integer id;
    private String brand;

    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(
            name = "developer_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_machine_developer_id")
    )
    private Developer developer;

    Machine() {
    }

    private Machine(final Integer id, final String brand, final Developer developer) {
        this.id = id;
        this.brand = brand;
        this.developer = developer;
    }

    public static Machine of(final Integer id, final String brand, final Developer developer) {
        return new Machine(id, brand, developer);
    }

    public Integer getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public Developer getDeveloper() {
        return developer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Machine)) return false;
        final Machine machine = (Machine) o;
        return Objects.equals(getId(), machine.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Machine{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                ", developer=" + developer +
                '}';
    }
}
