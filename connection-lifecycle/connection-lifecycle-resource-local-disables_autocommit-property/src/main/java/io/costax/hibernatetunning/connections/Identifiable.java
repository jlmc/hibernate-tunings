package io.costax.hibernatetunning.connections;

import java.io.Serializable;

public interface Identifiable<T extends Serializable> {

    T getId();
}
