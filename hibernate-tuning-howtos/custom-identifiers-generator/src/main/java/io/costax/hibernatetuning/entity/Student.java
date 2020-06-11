package io.costax.hibernatetuning.entity;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(generator = "student-id-generator")
    @GenericGenerator(
            name = "student-id-generator",
            parameters = @Parameter(
                    name = "prefix",
                    value = "AS-"),
            strategy = "io.costax.hibernatetuning.generator.MyCustomGenerator")
    private String id;

    private String name;

    public Student() {
    }

    public Student(final String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
