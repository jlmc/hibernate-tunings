package io.github.jlmc.valueobjects;

public record Details(String model, int year, String description) {

    public Details copyWithModel(String model) {
        return new Details(model, this.year, this.description);
    }

    public Details copyWithYear(int year) {
        return new Details(this.model, year, this.description);
    }
}
