package io.costa.hibernatetunings.entities;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "machine")
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Long id;

    @NaturalId
    @Column(name = "mac_address", nullable = false, unique = true, length = 48)
    private String macAddress;

    @Enumerated(EnumType.ORDINAL)
    @Column(columnDefinition = "smallint", nullable = false)
    Type type = Type.PC;

    @Column(name = "buy_day", nullable = false)
    private LocalDate buyDay;

    @Column(name = "start")
    private LocalTime start;

    public Machine() {
    }

    private Machine(final String macAddress, final Type type, final LocalDate buyDay, final LocalTime start) {
        this.macAddress = macAddress;
        this.type = type;
        this.buyDay = buyDay;
        this.start = start;
    }

    public static Machine of(final String macAddress, final Type type, final LocalDate buyDay, final LocalTime start) {
        return new Machine(macAddress, type, buyDay, start);
    }

    public enum Type {
        MAC, PC
    }

}
