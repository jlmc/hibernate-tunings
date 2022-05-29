package io.costax.hibernatetunings.entities;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.costax.hibernatetunings.entities.base.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.Type;

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

    @Type(value = JsonBinaryType.class)
    @Column(name = "location")
    private Location location;

    @Type(value = IntArrayType.class)
    @Column(name = "price_base_values", columnDefinition = "integer[]")
    private int[] priceBaseValues = {};

    @Type(value = StringArrayType.class)
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
