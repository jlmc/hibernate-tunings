package io.costax.hibernatetunnig.graphs.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Employee {

    @Id
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "publisher_id")
    private Publisher publisher;
}
