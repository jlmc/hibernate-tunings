package io.costax.hibernatetuning.enhance.model;

import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Dependency {

    @Id
    private Integer id;

    @NaturalId
    private String code;
}
