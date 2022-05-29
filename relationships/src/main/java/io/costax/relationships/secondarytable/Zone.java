package io.costax.relationships.secondarytable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.DynamicUpdate;

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
