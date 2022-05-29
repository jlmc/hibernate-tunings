package io.costax.hibernatetunings.entities.financial;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "financial_document")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
// by default the @DiscriminatorColumn is not necessary, the dtype  is used by default already, i'm using this configuration just for documentation the example.
// if we use DiscriminatorColumn with a no  DiscriminatorType.STRING discriminatorType then we also need to define the @DiscriminatorValue in the subclasses.
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING, columnDefinition = "varchar")
public abstract class FinancialDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private short version;

    @CreationTimestamp
    @Column(name = "created_on")
    private OffsetDateTime createAt;

    @Column(name = "value")
    private BigDecimal value = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, updatable = false)
    private Board board;

    protected FinancialDocument() {
    }

    protected FinancialDocument(final BigDecimal value) {
        this.value = value;
    }

    protected void setBoard(final Board board) {
        this.board = board;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FinancialDocument that = (FinancialDocument) o;
        return this.id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
