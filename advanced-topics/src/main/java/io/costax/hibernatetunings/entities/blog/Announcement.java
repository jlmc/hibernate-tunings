package io.costax.hibernatetunings.entities.blog;

import javax.persistence.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Entity
@DiscriminatorValue("1")
public class Announcement extends Topic {

    @Temporal(TemporalType.DATE)
    @Column(name = "valid_until")
    private Date validUntil;

    protected Announcement() {
    }

    @Override
    public String toDescription() {
        var validInstant = Optional.ofNullable(validUntil).map(Date::getTime).map(Instant::ofEpochMilli).map(DateTimeFormatter.ISO_INSTANT::format).orElse("unknown");
        return String.format("Announcement: \"%s\" valid Until %s", getTitle(),
                validInstant);
    }

    Announcement(final String owner, final String title, final Date validUntil) {
        super(owner, title);
        this.validUntil = validUntil;
    }
}
