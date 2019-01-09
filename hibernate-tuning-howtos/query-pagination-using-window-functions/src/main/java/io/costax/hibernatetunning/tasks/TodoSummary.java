package io.costax.hibernatetunning.tasks;

public class TodoSummary {

    private final Long id;
    private final String title;
    private final Long numOfComments;

    public TodoSummary(final Long id, final String title, final Long numOfComments) {
        this.id = id;
        this.title = title;
        this.numOfComments = numOfComments;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getNumOfComments() {
        return numOfComments;
    }
}
