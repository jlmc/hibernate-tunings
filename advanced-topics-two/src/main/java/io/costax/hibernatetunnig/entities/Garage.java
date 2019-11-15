package io.costax.hibernatetunnig.entities;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.ALL;

@Entity
public class Garage {

    @Id
    private Integer id;

    private String description;

    /**
     * We can omit this property, but doing that we should make subtype collections updatable and insertable, not read only
     */
    @OneToMany(cascade = {ALL}, orphanRemoval = true)
    @JoinColumn(name = "garage_id")
    private List<Vehicle> vehicles = new ArrayList<>();

    /**
     * This property should be used for read only
     */
    @OneToMany(
            cascade = {
                    //ALL,
                    //PERSIST,
                    //MERGE,
                    //REMOVE,
                    //REFRESH,
                    //DETACH
            },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "garage_id", updatable = false)
    @Where(clause = "dtype = 'C'")
    private List<Car> cars = new ArrayList<>();

    /**
     * This property should be used for read only
     */
    @OneToMany(
            cascade = {
                    //ALL,
                    //PERSIST,
                    //MERGE,
                    //REMOVE,
                    //REFRESH,
                    //DETACH
            },
            fetch = FetchType.LAZY)
    @JoinColumn(name = "garage_id", updatable = false)
    @Where(clause = "dtype = 'B'")
    private List<Bicycle> bicycles = new ArrayList<>();

    protected Garage() {
    }

    private Garage(final Integer id, final String description) {
        this.id = id;
        this.description = description;
    }

    public static Garage of(final Integer id, final String description) {
        return new Garage(id, description);
    }

    public void add(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public void remove(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public List<Vehicle> getVehicles() {
        return Collections.unmodifiableList(vehicles);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Garage)) return false;
        final Garage garage = (Garage) o;
        return Objects.equals(id, garage.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Garage{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }


    public List<Car> getCars() {
        return List.copyOf(cars);
    }

    public List<Bicycle> getBicycles() {
        return List.copyOf(bicycles);
    }

    public void addBicycle(Bicycle bicycle) {
        // Caution: it is good idea to add the instance in the both collections to keep the both synchronized,
        // and one of them should not trigger any insert,update or delete
        this.bicycles.add(bicycle);
        this.vehicles.add(bicycle);
    }

    public void removeBicycle(Bicycle bicycle) {
        // Caution: it is good idea to remove the instance in the both collections to keep the both synchronized,
        // and one of them should not trigger any insert,update or delete
        this.bicycles.remove(bicycle);
        this.vehicles.remove(bicycle);
    }
}
