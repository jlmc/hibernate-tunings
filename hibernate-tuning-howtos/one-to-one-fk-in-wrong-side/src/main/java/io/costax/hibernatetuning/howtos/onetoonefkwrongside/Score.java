package io.costax.hibernatetuning.howtos.onetoonefkwrongside;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Score implements Serializable {

    private short home = 0;
    private short visitor = 0;

    public Score() {
    }

    public Score(final int home, final int visitor) {
        this.home = (short) home;
        this.visitor = (short) visitor;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Score score = (Score) o;
        return home == score.home &&
                visitor == score.visitor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(home, visitor);
    }
}
