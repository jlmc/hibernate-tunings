package oi.costax.hibernatetuning.howtos.onetoonefkwrongside;


import io.costax.hibernatetuning.howtos.onetoonefkwrongside.Game;
import io.costax.hibernatetuning.howtos.onetoonefkwrongside.GameReport;
import io.costax.hibernatetuning.howtos.onetoonefkwrongside.Score;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SaveOneToOneCascadeWithFKInWrongSideTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void setUp() {
        cleanData();
    }

    @AfterEach
    public void setDown() {
        cleanData();
    }

    private void cleanData() {
        provider.doInTx(em -> {

            final List<Game> allGames = em.createQuery("select g from Game g", Game.class).getResultList();

            allGames.forEach(System.out::println);

            System.out.println("****");

            allGames.forEach(em::remove);

            em.flush();
        });
    }

    @Test
    @Order(0)
    public void create_game_without_not_report() {
        provider.doInTx(em -> {

            final Game game = new Game("SLB", "VSC");

            em.persist(game);

        });
    }

    /**
     * mvn -Dtest=SaveOneToOneCascadeWithFKInWrongSideTest#d_create_gameWithReportTest test
     * In a good mapping this should not happen,
     * because there can be no game reports without the existence of a game...
     * so, FK is on the wrong side
     */
    @Test
    @Order(1)
    public void create_game_report_without_game() {
        provider.doInTx(em -> {

            byte[] bytes = "Unknow Game".getBytes(StandardCharsets.UTF_8);
            GameReport report = new GameReport(new Score(1, 0), bytes);
            em.persist(report);
        });
    }

    /**
     * mvn -Dtest=SaveOneToOneCascadeWithFKInWrongSideTest#d_create_gameWithReportTest test
     */
    @Test
    @Order(3)
    public void d_create_gameWithReportTest() {
        provider.doInTx(em -> {
            final Game game = new Game("Benfica", "Real Madrid");

            GameReport report = new GameReport(
                    new Score(5, 1),
                    "FC Barcelona vs Real Madrid".getBytes(StandardCharsets.UTF_8)
            );

            game.setReport(report);

            em.persist(game);

        });
    }


    @Test
    @Order(4)
    public void e_create_gameWithReportTestWithoutUsingCascade() {
        provider.doInTx(em -> {
            final Game game = new Game("Benfica", "MUN");

            em.persist(game);

            GameReport report = new GameReport(
                    new Score(3, 2),
                    "FC Barcelona vs Real Madrid".getBytes(StandardCharsets.UTF_8)
            );

            em.persist(report);

            game.setReport(report);
        });
    }


}
