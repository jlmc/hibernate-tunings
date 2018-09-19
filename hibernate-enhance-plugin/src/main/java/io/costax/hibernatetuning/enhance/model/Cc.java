package io.costax.hibernatetuning.enhance.model;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.util.Objects;

@Entity
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

    @ManyToOne//(fetch = FetchType.LAZY)
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
