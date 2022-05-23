package io.costax.hibernatetuning.p6syp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "t_dependency")
public class Dependency {

    @Id
    private Integer id;

    @NaturalId
    private String code;
}
