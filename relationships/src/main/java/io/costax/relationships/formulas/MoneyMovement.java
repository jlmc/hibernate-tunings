package io.costax.relationships.formulas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "money_movement")
public class MoneyMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Integer id;

    @Column(name = "value", nullable = false)
    private BigDecimal value = BigDecimal.ZERO;

    @Generated(GenerationTime.INSERT)
    @Column(name = "at", nullable = false, columnDefinition = "timestamp not null default current_timestamp")
    private OffsetDateTime at;

    protected MoneyMovement() {
    }

    private MoneyMovement(final BigDecimal value) {
        this.value = value;
    }

    protected static MoneyMovement of(final BigDecimal value) {
        return new MoneyMovement(value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MoneyMovement)) return false;
        final MoneyMovement that = (MoneyMovement) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getValue() {
        return value;
    }

    public OffsetDateTime getAt() {
        return at;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", value=" + value +
                ", at=" + at +
                '}';
    }
}
