package io.costax.hibernatetunings.entities.financial;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("Invoice")
public class Invoice extends FinancialDocument {

    @Column(name = "content")
    private String content;

    protected Invoice() {
    }

    private Invoice(final BigDecimal value, final String content) {
        super(value);
        this.content = content;
    }

    public static Invoice of(final BigDecimal value, final String content) {
        return new Invoice(value, content);
    }
}
