package io.costax.hibernatetunnig.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;

@Entity
@DiscriminatorValue("B")
public class Bicycle extends Vehicle {

    @NotNull
    @NotBlank
    @Pattern(regexp = "\\d{2}\\.\\d{2}")
    @Column(name = "wheel_size")
    private String wheelSize;


    protected Bicycle() {
        super();
    }

    public Bicycle(final Integer id, final String owner, @NotNull @NotBlank @Pattern(regexp = "\\d{2}\\.\\d{2}") final String wheelSize) {
        super(id, owner);
        this.wheelSize = wheelSize;
    }


    public static Bicycle of(final Integer id, final String owner, @NotNull @NotBlank @Pattern(regexp = "\\d{2}\\.\\d{2}") final String wheelSize) {
        return new Bicycle(id, owner, wheelSize);
    }

    public String getWheelSize() {
        return wheelSize;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Bicycle)) return false;
        if (!super.equals(o)) return false;
        final Bicycle bicycle = (Bicycle) o;
        return Objects.equals(getId(), bicycle.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "Bicycle{" +
                "id='" + getId() + '\'' +
                "owner='" + getOwner() + '\'' +
                "WheelSize='" + getWheelSize() + '\'' +
                '}';
    }
}
