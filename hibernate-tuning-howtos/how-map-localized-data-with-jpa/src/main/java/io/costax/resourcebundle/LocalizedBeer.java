package io.costax.resourcebundle;

import javax.persistence.*;
import java.util.Objects;

@Cacheable
@Entity
@Table(name = "t_localized_beer")
public class LocalizedBeer {

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id", nullable = false)
    private Beer beer;

    private String description;

    public LocalizedBeer() {
    }

    public LocalizedBeer(final Beer beer, final String locale, final String name, final String description) {
        this.beer = beer;
        this.description = description;
        this.localizedId = new LocalizedId(beer.getId(), locale, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizedBeer that = (LocalizedBeer) o;
        return Objects.equals(beer, that.beer) &&
                Objects.equals(localizedId, that.localizedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beer, localizedId);
    }

    @Override
    public String toString() {
        return "LocalizedBeer{" +
                "localizedId=" + localizedId +
                ", description='" + description + '\'' +
                '}';
    }
}
