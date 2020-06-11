package io.costax.relationships.onetoone;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Fetch a relationship @OneToOne that is mapped without 'MapsId' in a Lazy way,
 * Fix a n+1 issue  without use the hibernate enhance plugin.
 */
@JpaTest(persistenceUnit = "it")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class OneToOneFixNPlus1WithManualInstrumentationTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(OneToOneFixNPlus1WithManualInstrumentationTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_Fetch_OneToOne_relationship_in_lazy() {
        // create a user with a details
        provider.doInTx(em -> {
            final User user = new User();
            user.setId(1);
            user.setName("Duke");

            final Details details = new Details();
            details.setNickName("Odin");

            user.defineDetails(details);

            em.persist(user);
        });

        assertThatTheTotalNumberOfRecords(1, 1);
        assertThatTheTotalNumberOfDetailsOfTheUserId(1, 1);

        // Fetch the user instance and call get getDetails after detach the user
        provider.doIt(em -> {
            final User user = em.find(User.class, 1);
            em.detach(user);

            try {
                user.getDetails();
                fail("the user Instance is detached from the Persistence Context, " +
                        "so the getDetails should fail because the the details the fetch lazily!!!");

            } catch (HibernateException e) {
                LOGGER.info("Expected exception [{}], lazy initialization exception [{}]", e.getClass(), e.getMessage());
            }
        });

        // Fetch user instance closing the associated entity manager, and getDetails without a open EntityManager
        User unmanagedUserInstance = provider.doItWithReturn(em -> em.find(User.class, 1));
        assertNotNull(unmanagedUserInstance);

        try {
            unmanagedUserInstance.getDetails();

            fail("the user Instance is detached from the Persistence Context, " +
                    "so the getDetails should fail because the the details the fetch lazily!!!");
        } catch (LazyInitializationException e) {
            LOGGER.info("Expected exception [{}], lazy initialization exception [{}]", e.getClass(), e.getMessage());
        }

        // Fetch the user, and after that fetch the user details
        provider.doInTx(em -> {
            // HERE we are expecting that two one
            final User user = em.find(User.class, 1);
            System.out.println("=============");
            System.out.println(user.getName());
            System.out.println(Optional.ofNullable(user.getDetails()).map(Details::getNickName).orElse("-- no details --"));
            System.out.println("=============");
        });

        // Replace the  User.details, the Previous user.details must be deleted in cascade
        provider.doInTx(em -> {
            final User user = em.find(User.class, 1);

            user.defineDetails(null);

            // because we are using a unique in the user_id fk, and the INSERT statements are executed before the DELETES
            // we have to execute a flush method to clean the persistence context
            em.flush();

            final Details details = new Details();
            details.setNickName("Zeus");

            user.defineDetails(details);
            em.flush();
        });

        assertThatTheTotalNumberOfRecords(1, 1);
        assertThatTheTotalNumberOfDetailsOfTheUserId(1, 1);

        // Remove the User and in cascade the associated User.details
        provider.doInTx(em -> {
            final User user = em.getReference(User.class, 1);
            em.remove(user);
        });

        assertThatTheTotalNumberOfRecords(0, 0);
        assertThatTheTotalNumberOfDetailsOfTheUserId(0, 1);
    }

    private void assertThatTheTotalNumberOfRecords(int expectedNumberOfUserRecords, int expectedNumberOfDetailsRecords) {
        final int numberOfUserRecords = provider.doJDBCReturningWork(connection -> {
            try (final PreparedStatement stmt = connection.prepareStatement("select coalesce(count(id), 0) as v from user")) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

        final int numberOfDetailsRecords = provider.doJDBCReturningWork(connection -> {
            try (final PreparedStatement stmt = connection.prepareStatement("select coalesce(count(id), 0) as v from details")) {
                ResultSet rs = stmt.executeQuery();
                rs.next();
                return rs.getInt(1);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

        assertEquals(numberOfUserRecords, expectedNumberOfUserRecords);
        assertEquals(numberOfDetailsRecords, expectedNumberOfDetailsRecords);
    }

    private void assertThatTheTotalNumberOfDetailsOfTheUserId(final int expectedNumberOfDetailsRecords, final Integer userId) {
        int numberOfDetailsRecords = provider.doJDBCReturningWork(connection -> {
            try (final PreparedStatement stmt = connection.prepareStatement("select coalesce(count(id), 0) as v from details where user_id = ?")) {
                stmt.setInt(1, userId);

                ResultSet rs = stmt.executeQuery();
                rs.next();

                return rs.getInt(1);
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        });

        assertEquals(numberOfDetailsRecords, expectedNumberOfDetailsRecords);
    }
}