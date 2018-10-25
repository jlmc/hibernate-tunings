package io.costa.hibernatetunings.entities;

import io.costa.hibernatetunings.customtype.IPv4;
import io.costa.hibernatetunings.customtype.IPv4Type;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "machine")
@TypeDef(name = "ipv4", defaultForType = IPv4.class, typeClass = IPv4Type.class)
public class Machine {

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "smallint", nullable = false)
    Type type = Type.PC;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @NaturalId
    @Column(name = "mac_address", nullable = false, unique = true, length = 48)
    private String macAddress;

    @Column(name = "buy_day", nullable = false)
    private LocalDate buyDay;

    //@Column(name = "start", columnDefinition = "time")
    // private LocalTime start;

    @Column(name = "last_know_ip", columnDefinition = "inet")
    private IPv4 lastKnowIp;

    public Machine() {
    }

    private Machine(final String macAddress, final Type type, final LocalDate buyDay) {
        this.macAddress = macAddress;
        this.type = type;
        this.buyDay = buyDay;
    }

    public static Machine of(final String macAddress, final Type type, final LocalDate buyDay) {
        return new Machine(macAddress, type, buyDay);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Machine machine = (Machine) o;
        return Objects.nonNull(this.id) && Objects.equals(id, machine.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Long getId() {
        return id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public Type getType() {
        return type;
    }

    public LocalDate getBuyDay() {
        return buyDay;
    }

    public IPv4 getLastKnowIp() {
        return lastKnowIp;
    }

    public void setLastKnowIp(final IPv4 lastKnowIp) {
        this.lastKnowIp = lastKnowIp;
    }

    public enum Type {
        MAC, PC
    }

}
