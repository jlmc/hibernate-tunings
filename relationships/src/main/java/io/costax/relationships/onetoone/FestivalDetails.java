package io.costax.relationships.onetoone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
public class FestivalDetails {

    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "festival_id", referencedColumnName = "id", unique = true)
    private Festival festival;

    private String country;
    private String locality;
    private OffsetDateTime happensAt;

    public FestivalDetails() {}

    public FestivalDetails(final Integer id,
                           final Festival festival,
                           final String country,
                           final String locality,
                           final OffsetDateTime happensAt) {
        this.id = id;
        this.festival = festival;
        this.country = country;
        this.locality = locality;
        this.happensAt = happensAt;
    }

    public void setFestival(final Festival festival) {
        this.festival = festival;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FestivalDetails)) return false;
        final FestivalDetails that = (FestivalDetails) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FestivalDetails{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", locality='" + locality + '\'' +
                ", happensAt=" + happensAt +
                '}';
    }
}
