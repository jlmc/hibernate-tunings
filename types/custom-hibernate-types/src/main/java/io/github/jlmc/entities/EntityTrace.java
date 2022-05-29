package io.github.jlmc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "entity_trace")
public class EntityTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "snapshot")
    private String snapshot = "";

    //@org.hibernate.annotations.CreationTimestamp
    @Column(name = "created_at", columnDefinition = "timestamp with time zone DEFAULT now() NOT NULL")
    private Instant createdAt;
}
