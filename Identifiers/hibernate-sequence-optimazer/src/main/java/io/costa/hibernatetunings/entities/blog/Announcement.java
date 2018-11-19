package io.costa.hibernatetunings.entities.blog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
public class Announcement extends Topic {

    @Temporal(TemporalType.DATE)
    @Column(name = "valid_until")
    private Date validUntil;

    protected Announcement() {
    }

    Announcement(final String owner, final String title, final Date validUntil) {
        super(owner, title);
        this.validUntil = validUntil;
    }
}
