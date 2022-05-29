package io.github.jlmc.entities;

import io.github.jlmc.types.usetypes.BooleanArrayType;
import io.github.jlmc.types.usetypes.IntArrayType;
import io.github.jlmc.types.usetypes.TextArrayType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

import java.util.Arrays;

@Entity
@Table(name = "t_users")
public class User {

    @Id
    private Long id;

    @Column(columnDefinition = "text[]")
    @Type(value = TextArrayType.class)
    private String[] roles;

    @Column(columnDefinition = "int[]")
    @Type(value = IntArrayType.class)
    private int[] number = new int[0];

    @Column(columnDefinition = "boolean[]")
    @Type(value = BooleanArrayType.class)
    private boolean[] flags = new boolean[0];

    public User() {
    }

    User(Long id, String[] roles) {
        this.id = id;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public int[] getNumber() {
        return number;
    }

    public void setNumber(int[] number) {
        this.number = number;
    }

    public boolean[] getFlags() {
        return flags;
    }

    public void setFlags(boolean[] flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", roles=" + Arrays.toString(roles) +
                ", number=" + Arrays.toString(number) +
                ", flags=" + Arrays.toString(flags) +
                '}';
    }
}
