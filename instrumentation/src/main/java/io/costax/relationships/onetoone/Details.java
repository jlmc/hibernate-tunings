package io.costax.relationships.onetoone;

import jakarta.persistence.*;

@Entity
public class Details {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false, unique = true)
    private User user;

    private String nickName;

    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
