package io.costa.hibernatetunings.entities.blog;

import javafx.geometry.Pos;

import javax.persistence.Entity;

@Entity
public class Post extends Topic {

    private String  content;

    protected Post() {}

    public Post(final String content) {
        this.content = content;
    }


    public Post(final String owner, final String title, final String content) {
        super(owner, title);
        this.content = content;
    }
}
