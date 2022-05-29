package io.costax.hibernatetunnig.graphs.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Publisher {

    @Id
    private Long id;

    @Version
    private int version;
    private String name;

    @OneToMany(mappedBy = "publisher")
    private Set<Book> books = new HashSet<>();

    @OneToMany(mappedBy = "publisher", cascade = {CascadeType.ALL})
    private Set<Employee> employees = new HashSet<>();

    public Publisher() {
    }

    public Publisher(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }
}
