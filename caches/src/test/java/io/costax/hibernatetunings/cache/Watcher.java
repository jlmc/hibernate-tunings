package io.costax.hibernatetunings.cache;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;

public class Watcher implements TestRule {

    private final Logger logger;

    private Watcher(final Logger logger) {
        this.logger = logger;
    }

    public static Watcher timer(final Logger logger) {
        return new Watcher(logger);
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                final String methodName = description.getMethodName();
                logger.info("====> {} -- starts", methodName);

                final Instant timeBefore = Instant.now();

                base.evaluate();

                final Instant timeAfter = Instant.now();
                final Duration between = Duration.between(timeBefore, timeAfter);

                logger.info("====> {} -- ends in {}", methodName, between);
            }
        };
    }
}