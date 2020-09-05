package io.github.jlmc.batching.criteria;

import io.github.jlmc.batching.Match;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.List;

@JpaTest(persistenceUnit = "it")
public class CriteriaAPILiteralHandlingTest {

    @JpaContext
    JpaProvider cx;

    @BeforeEach
    void setUp() {
        cx.doInTx(em -> {

            em.persist(Match.of(LocalDate.now(), "RM CF", "FCB"));
            em.persist(Match.of(LocalDate.now(), "SCP", "SLB"));
            em.flush();
        });
    }

    @Test
    void string_values() {

        cx.doIt(em -> {

            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<Match> cq = cb.createQuery(Match.class);
            final Root<Match> matches = cq.from(Match.class);
            cq.where(cb.equal(matches.get("awayTeam"), "SLB"));

            final List<Match> allMatches = em.createQuery(cq).getResultList();
        });

    }

    @Test
    void numeric_values() {

        cx.doIt(em -> {

            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<Match> cq = cb.createQuery(Match.class);
            final Root<Match> matches = cq.from(Match.class);
            cq.where(cb.equal(matches.get("version"), 1));

            final List<Match> allMatches = em.createQuery(cq).getResultList();
        });

    }


}
