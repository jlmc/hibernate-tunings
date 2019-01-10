package io.costax.hibernatetuning.enhance.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(schema = "nplusonetoone", name = "dependency")
public class Dependency {

    @Id
    private Integer id;

    @NaturalId
    private String code;
}
