package io.costax.relationships.formulas;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.JoinFormula;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "bank_account")
public class BankAccount {

    @Id
    private Integer id;

    private String owner;

    @Generated(GenerationTime.INSERT)
    @Column(name = "create_at", nullable = false, columnDefinition = "timestamp not null default current_timestamp")
    private OffsetDateTime createAt;

    @Version
    private int version;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_account_id",
            updatable = false,
            nullable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "money_movement_bank_account_id_fk"))
    private Set<MoneyMovement> movements = new HashSet<>();


    @Formula(" ( select coalesce( sum( y.value ), 0) from money_movement y where y.bank_account_id = id ) ")
    private BigDecimal balance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinFormula("("
            + " select y.id "
            + "from money_movement y "
            + "where y.bank_account_id = id "
            + "order by y.at desc limit 1 ) ")
    private MoneyMovement lastMovement;

    protected BankAccount() {
    }

    private BankAccount(final Integer id, final String owner) {
        this.id = id;
        this.owner = owner;
    }

    public static BankAccount of(final Integer id, final String owner) {
        return new BankAccount(id, owner);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BankAccount)) return false;
        final BankAccount that = (BankAccount) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public Integer getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public OffsetDateTime getCreateAt() {
        return createAt;
    }

    public void push(final BigDecimal value) {
        Objects.requireNonNull(value, "The Given value can't be null");

        final MoneyMovement movement = MoneyMovement.of(value.abs());

        this.balance = balance.add(movement.getValue());
        movements.add(movement);
    }

    public void pull(final BigDecimal value) {
        Objects.requireNonNull(value, "The Given value can't be null");

        final MoneyMovement movement = MoneyMovement.of(value.abs().negate());

        this.balance = balance.add(movement.getValue());
        movements.add(movement);
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public MoneyMovement getLastMovement() {
        return lastMovement;
    }
}
