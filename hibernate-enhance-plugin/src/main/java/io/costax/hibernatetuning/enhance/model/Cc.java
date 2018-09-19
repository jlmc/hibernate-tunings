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

    @OneToOne(mappedBy = "cc", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    //@LazyToOne(LazyToOneOption.NO_PROXY)
    private Document document;

    public static Cc of(final String description) {
        Cc cc = new Cc();
        cc.description = description;
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
}
