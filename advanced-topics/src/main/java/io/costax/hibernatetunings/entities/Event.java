package io.costax.hibernatetunings.entities;

import io.costax.hibernatetunings.entities.base.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event")
public class Event extends BaseEntity {

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "event_developer",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id"))
    Set<Developer> developers = new HashSet<>();
    @Column(name = "name")
    private String name;
    @Type(type = "jsonb")
    @Column(name = "location")
    private Location location;
    @Type(type = "int-array")
    @Column(name = "price_base_values", columnDefinition = "integer[]")
    private int[] priceBaseValues = {};
    @Type(type = "string-array")
    @Column(name = "ports_names", columnDefinition = "text[]")
    private String[] portsName = {};

    public Event() {
    }

    private Event(final String name, final Location location) {
        this.name = name;
        this.location = location;
    }

    public static Event of(final String name, final Location location) {
        return new Event(name, location);
    }

    @Transient
    public void registe(Developer developer) {
        final String code = String.format("EV-%d-%d", this.getId(), developer.getId());
        final Tiket tiket = Tiket.of(code, 30.8);

        this.developers.add(developer);
        developer.setTiket(tiket);
    }

    public void setPriceBaseValues(final int[] priceBaseValues) {
        this.priceBaseValues = priceBaseValues;
    }

    public void setPortsName(final String[] portsName) {
        this.portsName = portsName;
    }
}
