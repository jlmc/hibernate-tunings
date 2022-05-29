package io.costax.relationships.generatortype;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import java.util.Objects;

/**
 * If you’re using Spring Data,
 * you can do the same with the {@code @CreatedBy} and {@code @LastModifiedBy} annotations,
 * as this functionality can be integrated with the user authentication mechanism defined
 * by Spring Security via the AuditorAware mechanism.
 */
@Entity
public class Sensor {

    @Id
    private Integer id;

    @Column(name = "sensor_name")
    private String sensorName;


    @Column(name = "sensor_value")
    private String value;

    /**
     * Similar to the effect of
     * {@link org.hibernate.annotations.CreationTimestamp}
     */
    @Column(name = "created_by")
    @GeneratorType(
            type = LoggedUserGenerator.class,
            when = GenerationTime.INSERT
    )
    private String createdBy;

    /**
     * Similar to the effect of
     * {@link org.hibernate.annotations.UpdateTimestamp}
     * {@link org.hibernate.annotations.CreationTimestamp}
     */
    @Column(name = "updated_by")
    @GeneratorType(
            type = LoggedUserGenerator.class,
            when = GenerationTime.ALWAYS
    )
    private String updatedBy;

    public Sensor() {
    }

    public Sensor(final Integer id, final String sensorName, final String value) {
        this.id = id;
        this.sensorName = sensorName;
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        final Sensor sensor = (Sensor) o;
        return Objects.equals(getId(), sensor.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id=" + id +
                ", sensorName='" + sensorName + '\'' +
                ", value='" + value + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", updatedBy='" + updatedBy + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
}
