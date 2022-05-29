package io.costax.hibernatetunings.entities;

import com.vladmihalcea.hibernate.type.basic.Inet;
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
//@TypeDef(name = "ipv4", defaultForType = IPv4.class, typeClass = IPv4Type.class)
//@TypeDef(name = "macaddr", defaultForType = MacAddr.class, typeClass = MacAddrType.class)
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

    //@Column(name = "start", columnDefinition = "time")
    // private LocalTime start;


    @Column(name = "last_know_ip", columnDefinition = "inet")
    private Inet lastKnowIp;

    public Machine() {
    }

    private Machine(final Type type, final LocalDate buyDay) {
        this.type = type;
        this.buyDay = buyDay;
    }

    public static Machine of(final Type type, final LocalDate buyDay) {
        return new Machine(type, buyDay);
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


    public Type getType() {
        return type;
    }

    public LocalDate getBuyDay() {
        return buyDay;
    }

    public Inet getLastKnowIp() {
        return lastKnowIp;
    }


    public void setLastKnowIp(final Inet lastKnowIp) {
        this.lastKnowIp = lastKnowIp;
    }


    public enum Type {
        MAC, PC
    }

}
