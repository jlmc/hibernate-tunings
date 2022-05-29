package io.costax.hibernatetunnig.graphs.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
}
