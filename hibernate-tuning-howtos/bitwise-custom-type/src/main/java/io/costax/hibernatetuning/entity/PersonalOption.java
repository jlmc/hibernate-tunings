package io.costax.hibernatetuning.entity;

import io.costax.hibernatetuning.bitwise.Bitwiseable;

public enum PersonalOption implements Bitwiseable {
        //None(0),
        Option1(1),
        Option2(2),
        Option3(4),
        Option4(8);

    private final int wise;

    PersonalOption(final int i) {
        this.wise = i;
    }

    @Override
    public int getWise() {
        return wise;
    }

    @Override
    public Bitwiseable[] all() {
        return PersonalOption.values();
    }
}
