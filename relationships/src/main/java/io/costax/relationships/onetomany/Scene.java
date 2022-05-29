package io.costax.relationships.onetomany;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import java.util.Objects;

@Entity
public class Scene {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scene_id_generator")
    @SequenceGenerator(name="scene_id_generator", sequenceName = "scene_seq", allocationSize = 10, initialValue = 1)
    private Integer id;

    public String description;

    private int li = 0;

    protected Scene() {}

    private Scene(final int li, final String description) {
        this.description = description;
        this.li = li;
    }

    public static Scene of(int li, final String description) {
        return new Scene(li, description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Scene)) return false;
        final Scene scene = (Scene) o;
        return this.id != null && Objects.equals(id, scene.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public int getLi() {
        return li;
    }

    public String getDescription() {
        return description;
    }
}
