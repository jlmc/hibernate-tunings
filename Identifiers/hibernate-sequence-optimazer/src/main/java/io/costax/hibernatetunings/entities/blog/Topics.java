package io.costax.hibernatetunings.entities.blog;

import java.sql.Date;
import java.time.LocalDate;

public final class Topics {

    public static Post newPostWith(final String owner, final String title, final String content) {
        final Post post = new Post(owner, title, content);
        //topicStatistics(post);
        return post;
    }

    public static Announcement newAnnouncementWith(final String owner, final String title, final LocalDate valid) {
        final Date validUntil = Date.valueOf(valid);
        final Announcement announcement = new Announcement(owner, title, validUntil);
        //topicStatistics(announcement);
        return announcement;
    }

    public static TopicStatistic topicStatistics(Topic topic) {
        TopicStatistic ts = new TopicStatistic();
        ts.setTopic(topic);
        ts.incrementViews();
        return ts;
    }

}
