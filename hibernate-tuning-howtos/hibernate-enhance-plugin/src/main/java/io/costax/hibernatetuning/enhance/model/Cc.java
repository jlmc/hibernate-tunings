package io.costax.hibernatetuning.enhance.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import java.util.Objects;

@Entity
@Table(schema = "nplusonetoone", name = "cc")
public class Cc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    @ManyToOne//(fetch = FetchType.LAZY)
    @JoinColumn(name = "dependency_id")
    private Dependency dependency;

    @OneToOne(mappedBy = "cc", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private Document document;

    @ManyToOne //(fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "rh_id")
    private HumanResource humanResource;

    public static Cc of(final String description, HumanResource humanResource) {
        Cc cc = new Cc();
        cc.description = description;
        cc.humanResource = humanResource;
        return cc;
    }

    public void setDocument(Document document) {
        if (Objects.isNull(document) && Objects.nonNull(this.document)) {
            this.document.setCc(null);
        } else {
            document.setCc(this);
        }

        this.document = document;
    }

    @Override
    public String toString() {
        return "Cc{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
