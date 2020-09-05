package io.github.jlmc.batching.jdbc;

import io.github.jlmc.batching.Match;
import io.github.jlmc.batching.MatchEvent;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
public class SQLInjectionTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLInjectionTest.class);

    @JpaContext
    JpaProvider context;

    private Integer matchId;

    @BeforeEach
    void setUp() {
        this.matchId =
                context.doInTxWithReturn(em -> {

                    final Match match = Match.of(LocalDate.parse("2020-08-02"), "Benfica", "Braga");

                    final MatchEvent startTheMatch = MatchEvent.of(1, "start the Match");
                    final MatchEvent goal1 = MatchEvent.of(10, "Gol of Sport Lisboa e Benfica");
                    final MatchEvent interval = MatchEvent.of(45, "interval");


                    match.addEvent(startTheMatch);
                    match.addEvent(goal1);
                    match.addEvent(interval);

                    em.persist(match);

                    return match.getId();
                });
    }

    @Test
    @Disabled
    void testStatementUpdateDropTable() {
        final Integer eventId =
                context.doInTxWithReturn(em -> {

                    final MatchEvent event = em.createQuery("""
                                select me from MatchEvent me 
                                where me.minute = :minute
                                and me.match.id = :matchId
                            """, MatchEvent.class)
                            .setParameter("minute", 1)
                            .setParameter("matchId", matchId)
                            .getSingleResult();

                    event.setDescription("Whistle for the start!!!");

                    return event.getId();
                });

        context.doIt(em -> {
            final MatchEvent matchEvent = em.find(MatchEvent.class, eventId);

            assertNotNull(matchEvent);
            assertEquals("Whistle for the start!!!", matchEvent.getDescription());
        });


        try {

            updatePostCommentReviewUsingStatement(eventId, "'; DROP TABLE match_events; -- '");

        } catch (Exception e) {
            LOGGER.error("Failure", e);
        }


        // Caused by: org.postgresql.util.PSQLException: ERROR: relation "match_events" does not exist
        MatchEvent event =
                context.doInTxWithReturn(em -> em.find(MatchEvent.class, eventId));

        assertNotNull(event);
    }

    @Test
    @Disabled
    public void testPreparedStatementUpdateDropTable() {
        final Integer eventId =
                context.doItWithReturn(em -> {
                    final MatchEvent event = em.createQuery("""
                                select me from MatchEvent me 
                                where me.minute = :minute
                                and me.match.id = :matchId
                            """, MatchEvent.class)
                            .setParameter("minute", 1)
                            .setParameter("matchId", matchId)
                            .getSingleResult();

                    return event.getId();
                });

        updateMatchEventDescriptionUsingPreparedStatement(eventId, "Awesome");

        context.doInTx(entityManager -> {
            MatchEvent event = entityManager.find(MatchEvent.class, eventId);
            assertEquals("Awesome", event.getDescription());
        });

        try {
            updateMatchEventDescriptionUsingPreparedStatement(eventId, "'; DROP TABLE match_events; -- '");
        } catch (Exception e) {
            LOGGER.error("Failure", e);
        }

        context.doIt(entityManager -> {
            MatchEvent event = entityManager.find(MatchEvent.class, eventId);
            assertNotNull(event);
        });
    }


    @Test
    public void testPreparedStatementSelectAndWait() {
        assertEquals("start the Match", getMatchEventDescriptionUsingPreparedStatement("1", matchId));

        getMatchEventDescriptionUsingPreparedStatement("1 AND 1 >= ALL ( SELECT 1 FROM pg_locks, pg_sleep(10) )", matchId);

        assertEquals("start the Match", getMatchEventDescriptionUsingPreparedStatement("1", matchId));
    }


    @Test
    public void testJPQLSelectAndWait() {
        context.doInTx(entityManager -> {
            List<Match> posts = getMatchByTeam(
                    "Benfica' and " +
                            "FUNCTION('1 >= ALL ( SELECT 1 FROM pg_locks, pg_sleep(2) ) --',) is '"
            );
            assertEquals(1, posts.size());
        });
    }

    private List<Match> getMatchByTeam(String teamName) {
        return context.doItWithReturn(entityManager -> {
            return entityManager.createQuery(
                    "select p " +
                            "from Match p " +
                            "where" +
                            "   p.homeTeam = '" + teamName + "' or p.awayTeam = '" + teamName + "'", Match.class)
                    .getResultList();
        });
    }


    private void updatePostCommentReviewUsingStatement(Integer id, String description) {
        context.doInTx(entityManager -> {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(
                            "UPDATE match_events " +
                                    "SET description = '" + description + "' " +
                                    "WHERE id = " + id
                    );
                }
            });
        });
    }

    private void updateMatchEventDescriptionUsingPreparedStatement(Integer id, String description) {
        context.doInTx(entityManager -> {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE match_events " +
                                "SET description = '" + description + "' " +
                                "WHERE id = " + id
                )) {
                    statement.executeUpdate();
                }
            });
        });
    }

    private String getMatchEventDescriptionUsingPreparedStatement(String minute, Integer matchId) {
        return context.doItWithReturn(entityManager -> {
            Session session = entityManager.unwrap(Session.class);
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT description " +
                                "FROM match_events " +
                                "WHERE match_id = " + matchId + " " +
                                "AND minute = " + minute
                )) {
                    try (ResultSet resultSet = statement.executeQuery()) {
                        return resultSet.next() ? resultSet.getString(1) : null;
                    }
                }
            });
        });
    }
}
