package io.costax.hibernatetuning.enhance.model;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(schema = "nplusonetoone", name = "dependency")
public class Dependency {

    @Id
    private Integer id;

    @NaturalId
    private String code;
}
