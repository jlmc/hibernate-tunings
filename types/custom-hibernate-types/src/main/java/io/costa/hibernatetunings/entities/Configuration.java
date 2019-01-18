package io.costa.hibernatetunings.entities;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "configuration")
//@TypeDef(name = "string-array", defaultForType = String[].class, typeClass = StringArrayType.class)
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    private String tenant;

    /**
     * We can use any of the next two approaches.
     *
     * Eg. 1:
     * <code>
     *      @Type(type = "string-array")
     * </code>
     *
     *
     * Eg. 2:
     * <code>
     *     @Type(type = "io.costa.hibernatetunings.arrays.StringArrayType")
     * </code>
     *
     */
    @Type(type = "string-array")
    @Column(name = "roles", columnDefinition = "text[] default ARRAY []::text[]")
    private String[] roles = {};

    protected Configuration() {
    }

    public Configuration(final String tenant, final String[] roles) {
        this.tenant = tenant;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getTenant() {
        return tenant;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(final String[] roles) {
        if (roles == null) {
            this.roles = null;
        }

        assert roles != null;
        this.roles = Arrays.copyOf(roles, roles.length);
    }
}
