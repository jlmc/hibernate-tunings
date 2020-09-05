package io.github.jlmc.batching.flush;

import io.github.jlmc.batching.Match;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.LongType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@JpaTest(persistenceUnit = "it")
public class HibernateFlushModesTest {


    @JpaContext
    JpaProvider cx;

    @Test
    void auto() {
        final FlushMode mode = FlushMode.AUTO;

        cx.doInTx(em -> {

            final Session session = em.unwrap(Session.class);
            session.setHibernateFlushMode(mode);

            Match match = Match.of(LocalDate.now(), "Home", "Away");
            session.persist(match);
            //em.flush();


            final Long count = (Long) em.createNativeQuery("select count(*) as v from matches")
                    .unwrap(NativeQuery.class)
                    .addScalar("v", LongType.INSTANCE)
                    .setHibernateFlushMode(mode)
                    .getSingleResult();

            Assertions.assertEquals(1L, count);

        });
    }
}
