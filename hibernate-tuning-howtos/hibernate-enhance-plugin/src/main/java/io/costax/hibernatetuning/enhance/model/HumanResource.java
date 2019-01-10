package io.costax.hibernatetuning.enhance.model;

import javax.persistence.*;

@Entity
@Table(schema = "nplusonetoone", name = "humanresource")
public class HumanResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "username", nullable = false)
    private String username;

    protected HumanResource() {
    }

    private HumanResource(final String username, final String name) {
        this.username = username;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public static HumanResource of(String username, String name) {
        return new HumanResource(username, name);
    }

    @PrePersist
    @PreUpdate
    protected void usernameToLower() {
        this.username = username.toLowerCase();
    }
}
