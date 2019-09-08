package io.costax.relationships.onetoone;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Festival {

    @Id
    private Integer id;
    private String name;

    @OneToOne(mappedBy = "festival", cascade = CascadeType.ALL, orphanRemoval = true, optional = false, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private FestivalDetails details;

    public Festival() {}

    private Festival(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Festival createFestival(final Integer id, final String name) {
        return new Festival(id, name);
    }

    public void setDetails(final FestivalDetails details) {
        if (details == null) {
            if (this.details != null) {
                this.details.setFestival(null);
            }
        } else {
            details.setFestival(this);
        }
        this.details = details;
    }

    public FestivalDetails getDetails() {
        return details;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Festival)) return false;
        final Festival festival = (Festival) o;
        return Objects.equals(id, festival.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Festival{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
