package io.costax.relationships;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class OneToManyUnidirectionalWIth3TableTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    //Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void t00__testPersistOneToManyUnidirectionalWIth3Table() {
        final EntityManager em = provider.em();

        provider.beginTransaction();

        final TvChannel rtp1 = TvChannel.of("RTP-1", "RPT-1");

        rtp1.addProgram(TvProgram.of(LocalTime.of(14, 10), LocalTime.of(14, 5), "Voz do Cidadão"));
        rtp1.addProgram(TvProgram.of(LocalTime.of(14, 40), LocalTime.of(19, 58), "Portugal no Mundo"));
        rtp1.addProgram(TvProgram.of(LocalTime.of(19, 59), LocalTime.of(21, 00), "Telejornal"));


        final TvChannel rtp2 = TvChannel.of("RTP-2", "RTP-2");
        rtp2.addProgram(TvProgram.of(LocalTime.of(14, 25), LocalTime.of(15, 0), "Os Segredos do Super Heróis"));

        em.persist(rtp1);
        em.persist(rtp2);

        provider.commitTransaction();
    }

    @Test
    public void t01__testRemovePrograms() {
        provider.doInTx(em -> {

            final TvChannel eurosport1 = em.find(TvChannel.class, "EUROSPORT1");

            final List<TvProgram> programs = eurosport1.getPrograms().stream()
                    .filter(tvProgram -> LocalTime.of(18, 30).isAfter(tvProgram.getStart()))
                    .collect(Collectors.toList());

            programs.forEach(eurosport1::removeProgram);
        });

    }

    @Test
    public void t02__testRemoveTvChannelAndCascadeHimPrograms() {
        provider.doInTx(em -> {
            final TvChannel eurosport1 = em.find(TvChannel.class, "EUROSPORT1");

            em.remove(eurosport1);
        });
    }
}
