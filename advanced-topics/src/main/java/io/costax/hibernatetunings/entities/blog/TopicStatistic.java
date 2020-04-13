package io.costax.hibernatetunings.entities.blog;

import javax.persistence.*;

@Entity
@Table(name = "topic_statistics")
public class TopicStatistic {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "topic_id", nullable = false, updatable = false)
    private Topic topic;

    private int views = 0;

    void setTopic(final Topic topic) {
        this.topic = topic;
    }

    public void incrementViews() {
        views++;
    }
}
