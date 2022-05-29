package io.costax.hibernatetunings.entities;

import io.costax.hibernatetunings.customtype.IPv4;
import io.costax.hibernatetunings.customtype.IPv4Type;
import io.costax.hibernatetunings.customtype.MacAddr;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "machine")
public class Machine {

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "smallint", nullable = false)
    Type type = Type.PC;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @Column(name = "buy_day", nullable = false)
    private LocalDate buyDay;

    @org.hibernate.annotations.Type(value = IPv4Type.class)
    @Column(name = "last_know_ip", columnDefinition = "inet")
    private IPv4 lastKnowIp;

    @Column(name = "mac_address", columnDefinition = "macaddr")
    private MacAddr macAddress;

    public Machine() {
    }

    private Machine(final String macAddress, final Type type, final LocalDate buyDay) {
        this.macAddress = MacAddr.of(macAddress);
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

    public MacAddr getMacAddress() {
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
