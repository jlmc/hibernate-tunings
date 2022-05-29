package io.github.jlmc.entities;

import io.github.jlmc.spi.Replicable;
import io.github.jlmc.types.Inet;
import io.github.jlmc.types.usetypes.InetType;
import io.github.jlmc.valueobjects.Details;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

@Entity
@Replicable
public class Tooy {

    @Id
    private Long id;
    private String title;

    @Version
    private int version;

    /**
     * instead of a custom class we can also use a Map<String, String> details = new HashMap<>();
     */
    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(name = "details")
    @JdbcTypeCode(SqlTypes.JSON)
    private Details details;
    //

    @Type(value = InetType.class)
    @Column(name = "last_know_ip", columnDefinition = "inet")
    private Inet ip;

    public Tooy() {
    }

    public Tooy(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVersion() {
        return version;
    }

    /*
    public void addDetails(String key, String value) {
        HashMap<String, String> stringStringHashMap = new HashMap<>(this.details);
        stringStringHashMap.put(key, value);
        this.details = stringStringHashMap;
    }

    public void removeDetail(String key) {
        HashMap<String, String> stringStringHashMap = new HashMap<>(this.details);
        stringStringHashMap.remove(key);

        this.details = stringStringHashMap;
    }
     */

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public Inet getIp() {
        return ip;
    }

    public void setIp(Inet ip) {
        this.ip = ip;
    }
}
