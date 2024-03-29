package io.costax.relationships.manytoone;

import io.costax.relationships.manytomany.Developer;
import io.costax.relationships.manytomany.Project;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;


@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManyToManyUnidirectionalTest {

    private static final int STEVE_WOZNIAK_ID = 1;
    private static final int BILL_GATES_ID = 2;
    private static final int LARRY_PAGE_ID = 3;
    private static final int LINUS_TORVALDS_ID = 4;
    private static final int LARRY_ELLISON_ID = 5;
    private static final Developer STEVE_WOZNIAK = Developer.of(STEVE_WOZNIAK_ID, "Steve Wozniak");
    private static final Developer BILL_GATES = Developer.of(BILL_GATES_ID, "Bill Gates");
    private static final Developer LARRY_PAGE = Developer.of(LARRY_PAGE_ID, "Larry Page");
    private static final Developer LINUS_TORVALDS = Developer.of(LINUS_TORVALDS_ID, "Linus Torvalds");
    private static final Developer LARRY_ELLISON = Developer.of(LARRY_ELLISON_ID, "Larry Ellison");
    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_create_and_manager_project_developers() {
        // create some developers
        provider.doInTx(em -> {
            em.persist(STEVE_WOZNIAK);
            em.persist(BILL_GATES);
            em.persist(LARRY_PAGE);
            em.persist(LINUS_TORVALDS);
            em.persist(LARRY_ELLISON);
        });

        // create some project
        provider.doInTx(em -> {
            final Project osMainX = Project.of(1, "OS Main X");

            osMainX.addDeveloper(em.find(Developer.class, STEVE_WOZNIAK_ID));
            osMainX.addDeveloper(em.find(Developer.class, LARRY_ELLISON_ID));
            osMainX.addDeveloper(em.find(Developer.class, LARRY_PAGE_ID));
            osMainX.addDeveloper(em.find(Developer.class, BILL_GATES_ID));
            em.persist(osMainX);

            em.persist(Project.of(2, "Closed Y"));
            em.persist(Project.of(3, "Fire 2 Marc"));

            em.flush();
        });

        // remove some developers from the project and add some others
        provider.doInTx(em -> {
            final Project osMainX = em.find(Project.class, 1);

            //osMainX.addDeveloper(em.find(Developer.class, LARRY_PAGE_ID));
            //osMainX.addDeveloper(em.find(Developer.class, LARRY_PAGE_ID));


            final Developer linusTorvalds = em.find(Developer.class, LINUS_TORVALDS_ID);
            osMainX.addDeveloper(linusTorvalds);

            osMainX.addDeveloper(Developer.of(91, "Steve JObs"));

            final Developer billGates = em.find(Developer.class, BILL_GATES_ID);
            osMainX.removeDeveloper(billGates);
            final Developer larryEllison = em.find(Developer.class, LARRY_ELLISON_ID);
            osMainX.removeDeveloper(larryEllison);


            em.flush();
        });


        // How many developers have the project?
        provider.doIt(em -> {

            final Project osMainX = em.find(Project.class, 1);

            osMainX.getTeamSize();

            Assertions.assertEquals(4, osMainX.getTeamSize());
            Assertions.assertTrue(osMainX.getDevelopers().containsAll(
                    List.of(
                            STEVE_WOZNIAK,
                            LARRY_PAGE,
                            LINUS_TORVALDS,
                            Developer.of(91, "Steve JObs"))));
        });

    }
}
