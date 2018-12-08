package oi.costax.hibernatetuning.howtos.onetoonefkwrongside;


import io.costax.hibernatetuning.howtos.onetoonefkwrongside.Game;
import io.costax.hibernatetuning.howtos.onetoonefkwrongside.GameReport;
import io.costax.hibernatetuning.howtos.onetoonefkwrongside.Score;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class SaveOneToOneCascadeWithFKInWrongSideTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_cleanAnyPreviusData() {
        provider.beginTransaction();
        final List<Game> allGames = provider.em().createQuery("select g from Game g", Game.class).getResultList();

        allGames.forEach(System.out::println);
        System.out.println("****");
        allGames.forEach(game -> provider.em().remove(game));

        provider.em().flush();

        provider.commitTransaction();
    }

    @Test
    public void b_createGameWithoutNotReport() {
        provider.beginTransaction();

        final Game game = new Game("SLB", "VSC");

        provider.em().persist(game);

        provider.commitTransaction();
    }

    /**
     * mvn -Dtest=SaveOneToOneCascadeWithFKInWrongSideTest#d_create_gameWithReportTest test
     * In a good mapping this should not happen,
     * because there can be no game reports without the existence of a game...
     * so, FK is on the wrong side
     */
    @Test
    public void c_createGameReportWitoutGame() {
        provider.beginTransaction();

        byte[] bytes = "Unknow Game".getBytes(StandardCharsets.UTF_8);
        GameReport report = new GameReport(new Score(1, 0), bytes);
        provider.em().persist(report);
        provider.commitTransaction();
    }

    /**
     * mvn -Dtest=SaveOneToOneCascadeWithFKInWrongSideTest#d_create_gameWithReportTest test
     */
    @Test
    public void d_create_gameWithReportTest() {
        provider.beginTransaction();

        final Game game = new Game("Benfica", "Real Madrid");

        GameReport report = new GameReport(
                new Score(5, 1),
                "FC Barcelona vs Real Madrid".getBytes(StandardCharsets.UTF_8)
        );

        game.setReport(report);

        provider.em().persist(game);

        provider.commitTransaction();
    }


    @Test
    public void e_create_gameWithReportTestWithoutUsingCascade() {
        provider.beginTransaction();

        final Game game = new Game("Benfica", "MUN");

        provider.em().persist(game);

        GameReport report = new GameReport(
                new Score(3, 2),
                "FC Barcelona vs Real Madrid".getBytes(StandardCharsets.UTF_8)
        );

        provider.em().persist(report);

        game.setReport(report);

        provider.commitTransaction();
    }

    @Test
    public void f_remove() {
        provider.beginTransaction();

        final Game game = provider.em().find(Game.class, 10L);

        game.setReport(null);

        provider.commitTransaction();
    }
}
