package io.costax.hibernatetunnig.overrideIdstrategy.overrideIdstrategy;

import io.costax.hibernatetunnig.overrideIdstrategy.entity.ProgramingLanguage;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.hibernate.Session;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
public class OverrideIdStrategyTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void before() {
        final String[] programingLanguagesNames = {
                "java", "javascript", "csharp", "sql"
        };

        provider.doInTx(em -> {
            for (String pn : programingLanguagesNames) {
                ProgramingLanguage programingLanguage = ProgramingLanguage.of(pn);
                em.persist(programingLanguage);
            }

            em.flush();
        });
    }

    @AfterEach
    public void after() {
        provider.doInTx(em -> {

            em.createQuery("select pl from ProgramingLanguage pl", ProgramingLanguage.class)
                    .getResultStream()
                    .forEach(em::remove);

            em.flush();
        });

    }

    @Test
    public void useAssignedIds() {
        provider.doInTx(em -> {
            em.merge(ProgramingLanguage.of(9001L, "python"));
            em.merge(ProgramingLanguage.of(9002L, "scala"));

            em.flush();
        });

        final EntityManager em = provider.em();

        final List<ProgramingLanguage> resultList = em.createQuery("select pl from ProgramingLanguage  pl", ProgramingLanguage.class).getResultList();
        assertEquals(6, resultList.size());

        final ProgramingLanguage python = resultList.stream().filter(pl -> pl.getId() == 9001L).findFirst().orElse(null);
        final ProgramingLanguage scala = resultList.stream().filter(pl -> pl.getId() == 9002L).findFirst().orElse(null);

        em.close();

        assertNotNull(python);
        assertNotNull(scala);
        assertEquals("python", python.getName());
        assertEquals("scala", scala.getName());

    }

    @Test
    public void shouldLoadUsingNaturalId() {
        //final Board tbone = unwrap.byNaturalId(Board.class).using("code", "t-bone").getReference();
        //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").load();
        //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").with(LockOptions.UPGRADE).load();
        //Board b = unwrap.bySimpleNaturalId(Board.class).with(LockOptions.UPGRADE).load("t-bone");

        final ProgramingLanguage java = provider.doItWithReturn(em ->
                em.unwrap(Session.class)
                        .bySimpleNaturalId(ProgramingLanguage.class)
                        .load("java"));

        assertNotNull(java);
    }

    @Test
    public void selectWithoutFetch() {

        final List<ProgramingLanguage> programingLanguages = provider.doItWithReturn(
                em -> em
                        .createQuery("select c from ProgramingLanguage c", ProgramingLanguage.class)
                        .getResultList());

        programingLanguages.forEach(System.out::println);
    }

}