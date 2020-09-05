package io.github.jlmc.batching.cascade;

import io.github.jlmc.batching.Match;
import io.github.jlmc.batching.MatchEvent;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@JpaTest(persistenceUnit = "it")
public class CascadingParentChildEntityStateTransitions {

    @JpaContext
    JpaProvider context;

    @Test
    void createMatchWithEvents() {

        context.doInTx(em -> {

            for (int i = 1; i <= 3; i++) {

                final Match match =
                        Match.of(LocalDate.parse("2020-08-10"), "home-" + i, "away-" + i)
                                .addEvent(MatchEvent.of(1, "Start Match : " + i));

                em.persist(match);
            }
        });

    }

    @Test
    void updateMatchWithEvents() {
        List<Integer> matchIds =
        //@formatter:off
        context.doInTxWithReturn(em -> {
            List<Integer> aggregate = new ArrayList<>();

            for (int i = 1; i <= 3; i++) {

                final Match match =
                        Match.of(LocalDate.parse("2020-08-10"), "home-" + i, "away-" + i)
                                .addEvent(MatchEvent.of(1, "Start Match : " + i));

                em.persist(match);

                aggregate.add(match.getId());

            }
            return aggregate;
        });
        //@formatter:on

        context.doInTx(em -> {

            for (final Integer matchId : matchIds) {

                final Match match = em.find(Match.class, matchId);
                match.setAwayTeam(match.getAwayTeam() + " (updated)");

                for (final MatchEvent event : match.getEvents()) {
                    event.setDescription(event.getDescription() + " (updated)");
                }

            }

        });

    }
}
