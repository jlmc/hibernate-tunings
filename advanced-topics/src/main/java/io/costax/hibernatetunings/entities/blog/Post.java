package io.costax.hibernatetunings.entities.blog;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Entity
@DiscriminatorValue("0")
public class Post extends Topic {

    private String content;

    protected Post() {
    }

    public Post(final String content) {
        this.content = content;
    }

    public Post(final String owner, final String title, final String content) {
        super(owner, title);
        this.content = content;
    }

    @Override
    public String toDescription() {
        return String.format("Post: \"%s\" content \"%s\"", getTitle(),  content);
    }
}
