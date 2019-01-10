package io.costax.hibernatetuning.enhance.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "nplusonetoone", name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "cc_id")
    private Cc cc;

    protected void setCc(final Cc o) {
        this.cc = o;
    }
}
