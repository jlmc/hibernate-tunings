package io.github.jlmc.entities;

import io.github.jlmc.spi.Replicable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
@Replicable
public class Tooy {

    @Id
    private Long id;
    private String title;

    @Version
    private int version;

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
}
