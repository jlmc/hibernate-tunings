package io.costax.hibernatetunings.entities.blog;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

@Entity
@DiscriminatorValue("1")
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
