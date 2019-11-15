package io.costax.hibernatetunnig.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// by default the @DiscriminatorColumn is not necessary, the dtype  is used by default already, i'm using this configuration just for documentation the example.
// if we use DiscriminatorColumn with a no  DiscriminatorType.STRING discriminatorType then we also need to define the @DiscriminatorValue in the subclasses.
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar")
public abstract class Vehicle {

    @Id
    private Integer id;
    private String owner;

    protected Vehicle() {
    }

    protected Vehicle(final Integer id, final String owner) {
        this.id = id;
        this.owner = owner;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle)) return false;
        final Vehicle vehicle = (Vehicle) o;
        return Objects.equals(id, vehicle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }
}
