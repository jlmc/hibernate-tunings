package io.costax.hibernatetunings.entities;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import io.costax.hibernatetunings.entities.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.util.Objects;

@Entity
@Table(name = "developer")
public class Developer extends BaseEntity {

    @Column(name = "name")
    private String nome;

    @Type(value = JsonBinaryType.class)
    @Column(name = "tiket", columnDefinition = "jsonb")
    private Tiket tiket;

    public Developer() {
    }

    private Developer(final String nome, final Tiket tiket) {
        this.nome = nome;
        this.tiket = tiket;
    }

    public String getNome() {
        return nome;
    }

    public Tiket getTiket() {
        return tiket;
    }

    public void setTiket(final Tiket tiket) {
        this.tiket = tiket;
    }

    public static class Builder {
        private String nome;
        private Tiket tiket;

        public Builder setNome(final String nome) {
            this.nome = nome;
            return this;
        }

        public Builder setTiket(final Tiket tiket) {
            this.tiket = tiket;
            return this;
        }

        public Developer createDeveloper() {
            return new Developer(nome, tiket);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Developer developer = (Developer) o;
        return getId() != null && Objects.equals(getId(), developer.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }
}
