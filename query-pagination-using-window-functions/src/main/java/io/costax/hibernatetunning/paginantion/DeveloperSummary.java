package io.costax.hibernatetunning.paginantion;

public class DeveloperSummary {

    private Long id;
    private String name;
    private Long numOfProgramingLanguages;

    public DeveloperSummary(final Long id, final String name, final Long numOfProgramingLanguages) {
        this.id = id;
        this.name = name;
        this.numOfProgramingLanguages = numOfProgramingLanguages;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getNumOfProgramingLanguages() {
        return numOfProgramingLanguages;
    }
}
