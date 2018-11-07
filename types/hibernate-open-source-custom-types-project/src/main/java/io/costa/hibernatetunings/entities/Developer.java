package io.costa.hibernatetunings.entities;

import io.costa.hibernatetunings.entities.base.BaseEntity;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "depeloper")
public class Developer extends BaseEntity {

    @Column(name = "name")
    private String nome;

    @Type(type = "jsonb")
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
}
