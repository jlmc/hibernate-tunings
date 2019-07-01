package io.costax.relationships;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@DynamicUpdate
@Entity
@Table(name = "zone")
@SecondaryTable(
        name = "zone_details",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "zone_id", referencedColumnName = "id")
)
public class Zone {

    @Id
    private Integer id;
    private String name;

    @Version
    private int version;

    @Column(table = "zone_details")
    private String pseudonym;


    public Zone() {
    }

    public Zone(final Integer id, final String name, final String pseudonym) {
        this.id = id;
        this.name = name;
        this.pseudonym = pseudonym;
    }

    public void setPseudonym(final String pseudonym) {
        this.pseudonym = pseudonym;
    }
}
