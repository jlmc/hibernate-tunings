package io.costax.hibernatetunnig.overrideIdstrategy.generators;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

    T getId();
}
