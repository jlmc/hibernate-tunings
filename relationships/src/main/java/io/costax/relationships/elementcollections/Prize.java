package io.costax.relationships.elementcollections;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Embeddable
public class Prize implements Serializable {

    private OffsetDateTime at;
    private BigDecimal value;

    protected Prize() {
    }

    private Prize(final OffsetDateTime at, final BigDecimal value) {
        this.at = at;
        this.value = value;
    }

    public static Prize of(final OffsetDateTime at, final BigDecimal value) {
        return new Prize(at, value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Prize)) return false;
        final Prize prize = (Prize) o;
        return Objects.equals(at, prize.at) &&
                Objects.equals(value, prize.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(at, value);
    }
}
