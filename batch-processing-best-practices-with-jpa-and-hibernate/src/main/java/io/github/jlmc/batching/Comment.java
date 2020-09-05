package io.github.jlmc.batching;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Comment {

    @Id
    private Integer id;

    private String pk;

    public Comment() {
    }

    public Comment(final Integer id, final String pk) {
        this.id = id;
        this.pk = pk;
    }
}
