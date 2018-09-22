package io.costax.hibernatetuning.p6syp.entities;

import javax.persistence.*;

@Entity
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

    public static HumanResource of (String username, String name) {
        return new HumanResource(username, name);
    }

    @PrePersist
    @PreUpdate
    protected void usernameToLower() {
        this.username = username.toLowerCase();
    }

    @Override
    public String toString() {
        return "HumanResource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
