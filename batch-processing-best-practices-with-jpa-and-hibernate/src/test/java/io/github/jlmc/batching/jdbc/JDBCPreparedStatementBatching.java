package io.github.jlmc.batching.jdbc;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.hibernate.internal.SessionImpl;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@JpaTest(persistenceUnit = "it")
public class JDBCPreparedStatementBatching {

    @PersistenceContext
    EntityManager em;

    @Test
    void using_batching_prepared_statement() throws SQLException {

        Connection connection = em.unwrap(SessionImpl.class).connection();

        try (PreparedStatement matchStatement = connection.prepareStatement("""
                insert into matches (id, at, hometeam, awayteam, version) 
                values (?, ?, ?, ?, ?)
                """)) {

            matchStatement.setInt(1, 1);
            matchStatement.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-15")));
            matchStatement.setString(3, "Benfica");
            matchStatement.setString(4, "Gil Vicente");
            matchStatement.setInt(5, 0);
            matchStatement.addBatch();

            matchStatement.setInt(1, 2);
            matchStatement.setDate(2, java.sql.Date.valueOf(LocalDate.parse("2020-01-20")));
            matchStatement.setString(3, "Benfica");
            matchStatement.setString(4, "Real Madrid");
            matchStatement.setInt(5, 0);
            matchStatement.addBatch();

            final int[] updateCounts = matchStatement.executeBatch();

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
        }

    }
}
