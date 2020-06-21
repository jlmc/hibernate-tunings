package io.costax.jpa.util;

import io.costax.jpa.util.providers.Database;

public abstract class HSQLDBJPATest extends AbstractTest {

    @Override
    protected Database database() {
        return Database.HSQLDB;
    }
}
