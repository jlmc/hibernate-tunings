package io.costax.resourcebundle;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LocalizedId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String locale;
    private String name;

    public LocalizedId() {
    }

    public LocalizedId(final Long id, final String locale, final String name) {
        this.id = id;
        this.locale = locale;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(final String locale) {
        this.locale = locale;
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
        if (!(o instanceof LocalizedId)) return false;
        final LocalizedId that = (LocalizedId) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(locale, that.locale) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, locale, name);
    }

    @Override
    public String toString() {
        return "LocalizedId{" +
                "id=" + id +
                ", locale='" + locale + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
