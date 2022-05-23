package io.costax.resourcebundle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKey;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "t_beer")
public class Beer {

    @Id
    private Long id;

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal price;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Version
    private int version;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "beer",
            orphanRemoval = true, // because we are using orphanRemoval we could omit CascadeType.REMOVE
            cascade = {CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.REMOVE})
    //@MapKey(name = "localizedId.locale")
    @MapKey(name = "localizedId.name")
    private Map<String, LocalizedBeer> localizations = new HashMap<>();

    protected Beer() {
    }

    public static Beer of(final long id, final String name, final BigDecimal price) {

        final Beer beer = new Beer();
        beer.id = id;
        beer.name = name;
        beer.price = price;
        return beer;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Beer)) return false;
        final Beer beer = (Beer) o;
        return Objects.equals(id, beer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Beer{" +
                "id=" + id +
                ", price=" + price +
                ", name='" + name + '\'' +
                '}';
    }

    public void addLocalization(String locale, String key, String text) {
        LocalizedBeer localizedBeer = new LocalizedBeer(this, locale, key, text);
        localizations.put(key, localizedBeer);
    }

    public void removeLocalization(String key) {
        localizations.remove(key);
    }
}
