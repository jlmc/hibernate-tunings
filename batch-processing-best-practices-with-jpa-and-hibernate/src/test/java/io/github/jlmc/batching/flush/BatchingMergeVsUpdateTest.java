package io.github.jlmc.batching.flush;

import io.github.jlmc.batching.Match;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@JpaTest(persistenceUnit = "it")
public class BatchingMergeVsUpdateTest {

    @JpaContext
    JpaProvider cx;

    private Match match;


    @BeforeEach
    void setUp() {
        cx.doInTx(em -> {

            match = Match.of(LocalDate.now(), "Home", "Away");
            em.persist(match);
            em.flush();
        });
    }

    @Test
    void using_jpa_merge() {

        cx.doInTx(em -> {
            em.clear();

            match.setHomeTeam("RM CF");
            match.setAwayTeam("FCB");

            em.merge(match);

        });

    }

    @Test
    void using_hibernate_update() {

        cx.doInTx(em -> {
            em.clear();

            final Session session = em.unwrap(Session.class);

            match.setHomeTeam("RM CF");
            match.setAwayTeam("FCB");

            session.update(match);

        });
    }
}
