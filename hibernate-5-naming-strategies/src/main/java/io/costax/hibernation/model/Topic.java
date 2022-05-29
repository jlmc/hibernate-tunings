package io.costax.hibernation.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String notes;

    private int numberOfItems = 1;

    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    Topic() {
    }

    private Topic(final String title, final String notes, final int numberOfItems) {
        this.title = title;
        this.notes = notes;
        this.numberOfItems = numberOfItems;
    }

    public static Topic createTopic(final String title, final String notes, final int numberOfItems) {
        return new Topic(title, notes, numberOfItems);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Topic topic = (Topic) o;
        return id != null && Objects.equals(id, topic.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{id=" + id +
                ", title='" + title + '\'' +
                ", numberOfItems=" + numberOfItems +
                '}';
    }

    public Long getId() {
        return id;
    }

    public byte[] getImage() {
        return image;
    }
}
