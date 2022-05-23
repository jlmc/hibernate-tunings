package io.costax.hibernatetunings.entities.blog;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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

    public Topic getTopic() {
        return topic;
    }
}
