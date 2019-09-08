package io.costax.relationships.generatortype;

import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;


/**
 * With the ValueGenerator interface,
 * Hibernate allows us to customize the way a given entity property is going to be generated.
 */
public class LoggedUserGenerator implements ValueGenerator<String> {

    @Override
    public String generateValue(final Session session, final Object owner) {
        return LoggedUserThreadLocal.get();
    }
}
