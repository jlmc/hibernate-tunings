package io.costax.trading.finex.entity;

import io.costax.core.json.JsonDocument;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * The Audit Log entity is in the database 2
 */

@JsonDocument
@Entity
public class AuditLog {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private Instant at;

    private String owner;

    private BigDecimal value = BigDecimal.ZERO;

    protected AuditLog() {
    }

    private AuditLog(final Instant at, final String owner, final BigDecimal value) {
        this.at = at;
        this.owner = owner;
        this.value = value;
    }

    public static AuditLog createAuditLog(final String owner, final BigDecimal value) {
        return new AuditLog(Instant.now(), owner, value);
    }

    void setAt(final Instant at) {
        this.at = at;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AuditLog auditLog = (AuditLog) o;
        return id != null && Objects.equals(id, auditLog.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", at=" + at +
                ", owner='" + owner + '\'' +
                ", value=" + value +
                '}';
    }
}
