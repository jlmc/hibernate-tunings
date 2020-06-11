package io.costax.hibernatetunning.paginantion;

import io.costax.hibernatetunning.tasks.Todo;
import io.costax.hibernatetunning.tasks.TodoComment;

import javax.persistence.EntityManager;

class DataPopulate implements AutoCloseable {

    private final EntityManager em;

    private DataPopulate(final EntityManager em) {
        this.em = em;
    }

    private static DataPopulate with(EntityManager em) {
        return new DataPopulate(em);
    }

    public static void executePopulate(EntityManager em) {
        try (DataPopulate instance = with(em)) {
            instance.populate();
        }
    }

    public static void executeCleanUp(EntityManager em) {
        try (DataPopulate instance = with(em)) {
            instance.clean();
        }
    }

    private void populate() {
        long commentsSeq = 1L;

        em.getTransaction().begin();

        em.createQuery("delete from TodoComment" ).executeUpdate();
        em.createQuery("delete from Todo " ).executeUpdate();
        em.flush();

        em.getTransaction().commit();
        em.getTransaction().begin();


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

        em.getTransaction().commit();
    }

    private void clean() {
        em.getTransaction().begin();
        em.createQuery("delete from TodoComment ").executeUpdate();
        em.createQuery("delete from Todo ").executeUpdate();
        em.getTransaction().commit();
    }

    @Override
    public void close() {
        if (this.em.isOpen()) {
            em.close();
        }
    }
}
