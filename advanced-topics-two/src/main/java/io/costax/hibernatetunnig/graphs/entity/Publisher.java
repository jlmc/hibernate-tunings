package io.costax.hibernatetunnig.graphs.entity;

import javax.persistence.*;
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

}
