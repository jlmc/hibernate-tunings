package io.costa.hibernatetunings.entities.blog;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dashboard")
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    private String name;

    @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Topic> topics = new ArrayList<>();

    protected Dashboard() {}

    public Dashboard(final String name) {
        this.name = name;
    }

    public static Dashboard of(final String name) {
        return new Dashboard(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Dashboard dashboard = (Dashboard) o;
        return id != null && Objects.equals(id, dashboard.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    public List<Topic> getTopics() {
        return Collections.unmodifiableList(topics);
    }

    public void addTopic(Topic topic) {
        this.topics.add(topic);
        topic.setDashboard(this);
    }


}
