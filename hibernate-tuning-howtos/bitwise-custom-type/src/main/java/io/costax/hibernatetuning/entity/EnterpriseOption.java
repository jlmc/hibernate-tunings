package io.costax.hibernatetuning.entity;

import io.costax.hibernatetuning.bitwise.Bitwiseable;

public enum EnterpriseOption implements Bitwiseable {
    //None(0),
    VIEW(1),
    PIVOT(2),
    OWNER(4),
    DRC(8),
    DEC(16);

    private final int wise;

    EnterpriseOption(final int wise) {
        this.wise = wise;
    }

    @Override
    public int getWise() {
        return wise;
    }

    @Override
    public Bitwiseable[] all() {
        return EnterpriseOption.values();
    }
}
