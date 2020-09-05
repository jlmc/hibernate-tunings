package io.github.jlmc.batching.jdbc;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JpaTest(persistenceUnit = "it")
public class HandlingJDBCBatchFailuresTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlingJDBCBatchFailuresTest.class);

    @PersistenceContext
    EntityManager em;
    private Connection connection;

    @BeforeEach
    void setUp() {
        this.connection = em.unwrap(SessionImpl.class).connection();
    }

    @Test
    void handlingJDBCBatchFailures() throws SQLException {

        try (PreparedStatement st = connection.prepareStatement(
                """
                        insert into matches (id, at, hometeam, awayteam, version) 
                        values (?, ?, ?, ?, ?)
                        """)) {

            for (int i = 1; i <= 3; i++) {

                st.setInt(1, i % 2);
                st.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-20")));
                st.setString(3, "Home " + i);
                st.setString(4, "Away " + i);
                st.setInt(5, 0);

                st.addBatch();
            }

            st.executeBatch();

            connection.commit();

        } catch (BatchUpdateException e) {
              LOGGER.info("Batch has managed to process {} entities", e.getUpdateCounts().length);
        }
    }
}
