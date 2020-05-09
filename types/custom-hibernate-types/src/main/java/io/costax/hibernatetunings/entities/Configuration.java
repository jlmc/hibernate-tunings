package io.costax.hibernatetunings.entities;

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
     * <p>
     * Eg. 1:
     * <code>
     *
     * @Type(type = "string-array")
     * </code>
     * <p>
     * <p>
     * Eg. 2:
     * <code>
     * @Type(type = "io.costax.hibernatetunings.arrays.StringArrayType")
     * </code>
     */
    @Type(type = "string-array")
    @Column(name = "roles", columnDefinition = "text[] default ARRAY []::text[]")
    private String[] roles = {};

    @Type(type = "int-array")
    @Column(name = "numbers", columnDefinition = "int[] default ARRAY []::int[]")
    private int[] numbers = {};

    protected Configuration() {
    }

    public Configuration(final String tenant, final String[] roles) {
        this.tenant = tenant;
        setRoles(roles);
    }

    public Configuration(final String tenant, final String[] roles, final int[] numbers) {
        this.tenant = tenant;
        setRoles(roles);
        setNumbers(numbers);
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
            return;
        }

        this.roles = Arrays.copyOf(roles, roles.length);
    }

    public int[] getNumbers() {
        return numbers;
    }

    public void setNumbers(final int[] n) {
        if (n == null) {
            this.numbers = null;
            return;
        }

        this.numbers = Arrays.copyOf(n, n.length);
    }
}
