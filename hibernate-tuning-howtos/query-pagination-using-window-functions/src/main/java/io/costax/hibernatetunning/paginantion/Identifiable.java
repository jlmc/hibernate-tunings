package io.costax.hibernatetunning.paginantion;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

    T getId();
}
