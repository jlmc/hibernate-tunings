package io.costax.hibernatetunning.paginantion;

import io.costax.hibernatetunning.tasks.Todo;
import io.costax.hibernatetunning.tasks.TodoComment;
import io.costax.rules.EntityManagerProvider;

import javax.persistence.EntityManager;
import java.util.Objects;

class DataPopulator {

    private final EntityManagerProvider provider;

    private DataPopulator(final EntityManagerProvider provider) {
        this.provider = provider;
    }

    static DataPopulator with(final EntityManagerProvider provider) {
        Objects.requireNonNull(provider);
        return new DataPopulator(provider);
    }

    void populate() {
        long commentsSeq = 1L;

        final EntityManager em = provider.em();
        provider.beginTransaction();

        for (int i = 1; i < 50; i++) {
            final Todo td = Todo.of((long) i, "todo-" + i);

            if (i == 1) {
                for (int y = 1; y < 4; y++) {
                    final String review = "todo-comment-" + td.getId() + "--" + y;
                    final TodoComment tc = TodoComment.of((commentsSeq++), review);
                    tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 3) {
                for (int y = 1; y < 2; y++) {
                    final String review = "todo-comment-" + td.getId() + "--" + y;
                    final TodoComment tc = TodoComment.of((commentsSeq++), review);
                    tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 5) {
                for (int y = 1; y < 3; y++) {
                    final TodoComment tc = TodoComment.of((commentsSeq++), "todo-comment-" + td.getId() + "--" + y);
                    // tc.setAttachment(review.getBytes());
                    td.addComment(tc);
                }
            }

            if (i == 13) {
                for (int y = 1; y < 7; y++) {
                    final TodoComment tc = TodoComment.of((commentsSeq++), "todo-comment-" + td.getId() + "--" + y);
                    td.addComment(tc);
                }
            }

            em.persist(td);
        }

        provider.commitTransaction();
    }

    void clean() {
        provider.beginTransaction();
        provider.em().createQuery("delete from TodoComment ").executeUpdate();
        provider.em().createQuery("delete from Todo ").executeUpdate();
        provider.commitTransaction();
    }
}
