package io.costa.hibernatetunings.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Embeddable
public class TimePeriod implements Serializable {

    @Column(name = "start", nullable = false)
    private OffsetDateTime start;

    @Column(name = "until", nullable = false)
    private OffsetDateTime until;

    protected TimePeriod() {
    }

    private TimePeriod(final OffsetDateTime start, final OffsetDateTime until) {
        this.start = start;
        this.until = until;
    }

    public static TimePeriod of(final OffsetDateTime start, final OffsetDateTime until) {
        return new TimePeriod(start, until);
    }

    public OffsetDateTime getStart() {
        return start;
    }

    public OffsetDateTime getUntil() {
        return until;
    }

    @Override
    public String toString() {
        return "TimePeriod{" +
                "start=" + start +
                ", until=" + until +
                '}';
    }


    public TimePeriod plusDays(final long days) {
        return of(this.start.plusDays(days), until.plusDays(days));
    }
}
