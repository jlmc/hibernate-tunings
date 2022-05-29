package io.costax.hibernatetunings.entities.financial;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@DiscriminatorValue("CreditNote")
public class CreditNote extends FinancialDocument {

    @Column(name = "expiration_on")
    private OffsetDateTime expirationOn;

    protected CreditNote() {
    }

    private CreditNote(final BigDecimal value, final OffsetDateTime expirationOn) {
        super(value);
        this.expirationOn = expirationOn;
    }

    public static CreditNote of(final BigDecimal value, final OffsetDateTime expirationOn) {
        return new CreditNote(value, expirationOn);
    }
}
