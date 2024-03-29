package io.costax.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;

@Entity
@Table(name = "client",
        uniqueConstraints = @UniqueConstraint(
                name = "client_slug_uk",
                columnNames = "slug"))
public class Client {

    @Id
    private Integer id;

    private String name;

    @NaturalId
    private String slug;

    @Column(name = "create_on", updatable = false, columnDefinition = "timestamp default current_timestamp")
    @Generated(GenerationTime.INSERT)
    private String createdOn;

    @Version
    private int version;

    protected Client() {
    }

    public Client(final Integer id, final String slug, final String name) {
        this.id = id;
        this.name = name;
        this.slug = slug;
    }

    public Integer getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Client client = (Client) o;
        return id != null && Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
