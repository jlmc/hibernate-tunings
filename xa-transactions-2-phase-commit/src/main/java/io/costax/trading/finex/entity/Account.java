package io.costax.trading.finex.entity;

import io.costax.core.json.JsonDocument;
import io.costax.core.persistence.AbstractAggregateRoot;
import io.costax.core.reflection.Reflections;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This entity is in the database 1
 */
@JsonDocument
@Entity
@EntityListeners(AccountEntityListener.class)
public class Account extends AbstractAggregateRoot<AuditLog> {

    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    private String owner;

    @NotNull
    private BigDecimal value = BigDecimal.ZERO;

    public Account() {}

    private Account(final Account source) {
        Reflections.copyFields(source, this, List.of("domainEvents"));
    }

    public static Account createNewAccount(String owner, BigDecimal value) {
        Account account = new Account();
        account.owner = owner;
        account.value = value;
        return account;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Account account = (Account) o;
        return id != null && Objects.equals(id, account.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + id +
                ", owner='" + owner + '\'' +
                ", value=" + value +
                '}';
    }

    public UUID getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void raise(BigDecimal value) {
        //BigDecimal original = this.value;
        BigDecimal negate = value.abs().negate();
        this.value = this.value.add(negate);

        addDomainEvent(AuditLog.createAuditLog(owner, negate));
    }

    public void deposit(BigDecimal value) {
        //BigDecimal original = this.value;
        BigDecimal positive = value.abs();
        this.value = this.value.add(positive);

        addDomainEvent(AuditLog.createAuditLog(owner, positive));
    }

    public Account copy() {
        Account account = new Account(this);
        account.addDomainEvent(AuditLog.createAuditLog(account.getOwner(), account.getValue()));
        return account;
    }
}
