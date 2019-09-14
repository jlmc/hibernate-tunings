package io.costax.relationships.onetoone;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import javax.persistence.*;

@Entity
public class User implements PersistentAttributeInterceptable {

    @Id
    private Integer id;
    private String name;

    @LazyToOne(LazyToOneOption.NO_PROXY)
    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user",
            optional = true,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Details details;

    @Transient
    private PersistentAttributeInterceptor interceptor;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Details getDetails() {
        if (interceptor != null) {
            return (Details) interceptor.readObject(this, "details", details);
        }
        return details;
    }

    void setDetails(final Details details) {
        if (this.interceptor != null) {
            this.details = (Details) this.interceptor.writeObject(this, "details", this.details, details);
            //return;
        }

        this.details = details;
    }

    public void defineDetails(Details d) {
        if (this.getDetails() != null) {
            this.getDetails().setUser(null);
            this.details = null;
        }

        if (d != null) {
            d.setUser(this);
        }
        this.details = d;
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return interceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(final PersistentAttributeInterceptor interceptor) {
        this.interceptor = interceptor;
    }
}
