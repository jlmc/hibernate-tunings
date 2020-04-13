package io.costax.hibernation.model;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
public class ShoppingList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    private OffsetDateTime createAt;

    private OffsetDateTime redLine;

    @OneToMany(fetch = FetchType.LAZY,
            // when the orphanRemoval is set with true the CascadeType.REMOVE is redundant
            orphanRemoval = true,
            // cascade = CascadeType.ALL
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }
    )
    //@OrderColumn(name = "entry")
    @JoinColumn(name = "shopping_list_id", nullable = false, updatable = false)
    private List<Topic> topics = new ArrayList<>();

    ShoppingList() {
    }

    private ShoppingList(final String description, final OffsetDateTime createAt, final OffsetDateTime redLine) {
        this.description = description;
        this.createAt = createAt;
        this.redLine = redLine;
    }

    public static ShoppingList createShoppingList(final String description, final OffsetDateTime createAt, final OffsetDateTime redLine) {
        return new ShoppingList(description, createAt, redLine);
    }

    public Topic addTopic(final String title, final String notes, final int numberOfItems) {
        Topic topic = Topic.createTopic(title, notes, numberOfItems);
        this.topics.add(topic);
        return topic;
    }

    /**
     * Removes the all occurrences of the specified element from this list,
     *
     * @return {@code true} if this list contained the specified element
     */
    public boolean removeTopic(Topic topic) {
        List<Topic> elementsToRemove = topics
                .stream()
                .filter(p -> Objects.equals(p, topic))
                .collect(Collectors.toList());

        if (elementsToRemove.isEmpty()) return false;

        return this.topics.removeAll(elementsToRemove);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ShoppingList that = (ShoppingList) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", redLine=" + redLine +
                '}';
    }

    public List<Topic> getTopics() {
        return List.copyOf(topics);
    }

}
