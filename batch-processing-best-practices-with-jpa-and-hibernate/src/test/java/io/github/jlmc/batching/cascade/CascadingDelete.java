package io.github.jlmc.batching.cascade;

import io.github.jlmc.batching.Match;
import io.github.jlmc.batching.MatchEvent;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.time.LocalDate.parse;

@JpaTest(persistenceUnit = "it")
public class CascadingDelete {

    @JpaContext
    JpaProvider cx;

    List<Integer> matchIds = new ArrayList<>();

    @BeforeEach
    void setUp() {

        cx.doInTx(em -> {
            for (int i = 1; i <= 3; i++) {

                final Match match =
                        Match.of(parse("2020-08-10"), "home-" + i, "away-" + i)
                                .addEvent(MatchEvent.of(1, "Start Match : " + i));

                em.persist(match);

                matchIds.add(match.getId());
            }
        });

    }

    @Test
    void deleteMatchAndEvents() {
        cx.doInTx(em -> {

            final List<Match> matches =
                    em.createQuery(
                     """
                     select m from Match m
                    """, Match.class)
                    .getResultList();

            matches.forEach(em::remove);
        });
    }

    @Test
    void cascading_DELETE_statements_Workaround_1() {
        cx.doInTx(em -> {
            final List<Match> matches =
                    em.createQuery(
                            """
                            select m from Match m
                           """, Match.class)
                            .getResultList();

            for (Match post : matches) {
                for (Iterator<MatchEvent> eventIterator = post.getEvents().iterator(); eventIterator.hasNext(); ) {
                    MatchEvent event = eventIterator.next();
                    event.setMatch(null);
                    eventIterator.remove();
                }
            }

            em.flush();

            matches.forEach(em::remove);
        });
    }

    @Test
    void cascading_DELETE_statements_Workaround_2() {
        cx.doInTx(em -> {

            final List<Match> matches =
                    em.createQuery(
                            """
                            select m from Match m
                           """, Match.class)
                            .getResultList();

           em.createQuery(
                   """
                   delete from MatchEvent me
                   where me.match in :matches
                   """
               )
               .setParameter("matches", matches)
               .executeUpdate();

            em.flush();

            matches.forEach(em::remove);
        });
    }
}
