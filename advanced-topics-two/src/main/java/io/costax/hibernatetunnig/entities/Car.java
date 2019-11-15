package io.costax.hibernatetunnig.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import java.util.Objects;


@Entity
@DiscriminatorValue("C")
public class Car extends Vehicle {

    @NotBlank
    @Column(name = "model")
    private String model;

    protected Car() {
        super();
    }

    private Car(final Integer id, final String owner, @NotBlank final String model) {
        super(id, owner);
        this.model = model;
    }


    public static Car of(final Integer id, final String owner, @NotBlank final String model) {
        return new Car(id, owner, model);
    }

    public String getModel() {
        return model;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        if (!super.equals(o)) return false;
        final Car car = (Car) o;
        return Objects.equals(getId(), car.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "Car{" +
                "id='" + getId() + '\'' +
                "owner='" + getOwner() + '\'' +
                "model='" + model + '\'' +
                '}';
    }
}


