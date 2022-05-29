package io.costax.relationships.onetomany;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OneToManyUnidirectionalWIth3TableTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    @Order(0)
    public void test_persist_one_to_many_unidirectional_with_3_tables() {
        provider.doInTx(em -> {

            final TvChannel rtp1 = TvChannel.of("RTP-1", "RPT-1");

            rtp1.addProgram(TvProgram.of(LocalTime.of(14, 10), LocalTime.of(14, 5), "Voz do Cidadão"));
            rtp1.addProgram(TvProgram.of(LocalTime.of(14, 40), LocalTime.of(19, 58), "Portugal no Mundo"));
            rtp1.addProgram(TvProgram.of(LocalTime.of(19, 59), LocalTime.of(21, 0), "Telejornal"));


            final TvChannel rtp2 = TvChannel.of("RTP-2", "RTP-2");
            rtp2.addProgram(TvProgram.of(LocalTime.of(14, 25), LocalTime.of(15, 0), "Os Segredos do Super Heróis"));

            em.persist(rtp1);
            em.persist(rtp2);

        });
    }

    @Test
    @Order(1)
    public void test_remove_programs() {
        provider.doInTx(em -> {

            final TvChannel eurosport1 = em.find(TvChannel.class, "EUROSPORT1");

            final List<TvProgram> programs = eurosport1.getPrograms().stream()
                    .filter(tvProgram -> LocalTime.of(18, 30).isAfter(tvProgram.getStart()))
                    .collect(Collectors.toList());

            programs.forEach(eurosport1::removeProgram);
        });

    }

    @Test
    @Order(3)
    public void test_remove_tv_channel_and_cascade_him_programs() {
        provider.doInTx(em -> {
            final TvChannel eurosport1 = em.find(TvChannel.class, "EUROSPORT1");

            em.remove(eurosport1);
        });
    }
}
