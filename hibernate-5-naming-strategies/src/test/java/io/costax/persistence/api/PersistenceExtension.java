package io.costax.persistence.api;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>Extension Points</h2>
 * <p>
 * JUnit Jupiter extensions can declare interest in certain junctures of the test life cycle.
 * When the JUnit Jupiter engine processes a test, it steps through these junctures and calls each registered extension.
 * In rough order of appearance, these are the extension points:
 * </p>
 *
 * <p>
 *     <ul>
 *          <li>instance post processor</li>
 *          <li>template invocation</li>
 *          <li>execution condition</li>
 *          <li>@BeforeAll callback</li>
 *          <li>@BeforeEach callback</li>
 *          <li>parameter resolution</li>
 *          <li>before test execution callback</li>
 *          <li>after test execution callback</li>
 *          <li>exception handling</li>
 *          <li>@AfterEach callback</li>
 *          <li>@AfterAll callback</li>
 *     </ul>
 * </p>
 */
public class PersistenceExtension implements BeforeAllCallback, AfterAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceExtension.class);

    private final String persistenceUnitName;

    private PersistenceExtension(final String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    public static PersistenceExtension withPersistenceUnit(final String persistenceUnitName) {
        return new PersistenceExtension(persistenceUnitName);
    }

    @Override
    public void beforeAll(final ExtensionContext context) {
        LOGGER.info("Initializing the EntityManagerFactory");

        ThreadLocalEntityManagerProvider.withPersistenceUnit(persistenceUnitName);
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        LOGGER.info("Shutdown the EntityManagerFactory");
        ThreadLocalEntityManagerProvider.shutdown();
    }
}
