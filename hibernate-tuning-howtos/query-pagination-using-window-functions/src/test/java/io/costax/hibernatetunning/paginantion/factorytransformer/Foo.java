package io.costax.hibernatetunning.paginantion.factorytransformer;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

public class Foo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer dendenciaId;
    private final BigDecimal value;

    private Foo(final Integer id, final BigDecimal value) {
        this.dendenciaId = id;
        this.value = value;
    }

    public static Foo of(final Integer id,
                         final BigDecimal value) {
        return new Foo(id, value);
    }

    @Override
    public String toString() {
        return "Foo{" +
                "dendenciaId=" + dendenciaId +
                ", value=" + value +
                '}';
    }
}
