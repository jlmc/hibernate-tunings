package io.costax.hibernatetunnig.overrideIdstrategy.overrideIdstrategy;

import io.costax.hibernatetunnig.overrideIdstrategy.entity.ProgrammingLanguage;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.junit.*;

import javax.persistence.EntityManager;
import java.util.List;


public class OverrideIdStrategyTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    public void before() {
        final String[] programingLanguagesNames = {
                "java", "javascript", "csharp", "sql"
        };

        provider.beginTransaction();

        for (String pn : programingLanguagesNames) {
            ProgrammingLanguage programmingLanguage = ProgrammingLanguage.of(pn);
            provider.em().persist(programmingLanguage);
        }

        provider.commitTransaction();
    }

    @After
    public void after() {
        provider.beginTransaction();
        final EntityManager em = provider.em();
        em.createQuery("select pl from ProgrammingLanguage pl", ProgrammingLanguage.class)
                .getResultStream()
                .forEach(em::remove);
        provider.commitTransaction();
    }

    @Test
    public void shoudlUseAsignedIds() {
        provider.beginTransaction();

        provider.em().merge(ProgrammingLanguage.of(9001L, "python"));
        provider.em().merge(ProgrammingLanguage.of(9002L, "scala"));

        provider.em().flush();
        provider.commitTransaction();


        final List<ProgrammingLanguage> resultList = provider.em().createQuery("select pl from ProgrammingLanguage  pl", ProgrammingLanguage.class).getResultList();
        Assert.assertEquals(6, resultList.size());

        final ProgrammingLanguage python = resultList.stream().filter(pl -> pl.getId() == 9001L).findFirst().orElse(null);
        final ProgrammingLanguage scala = resultList.stream().filter(pl -> pl.getId() == 9002L).findFirst().orElse(null);

        Assert.assertEquals("python", python.getName());
        Assert.assertEquals("scala", scala.getName());
    }

    @Test
    public void shouldLoadUsingNaturalId() {
        //final Board tbone = unwrap.byNaturalId(Board.class).using("code", "t-bone").getReference();
        //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").load();
        //Board b = unwrap.byNaturalId(Board.class).using(Board_.code, "t-bone").with(LockOptions.UPGRADE).load();
        //Board b = unwrap.bySimpleNaturalId(Board.class).with(LockOptions.UPGRADE).load("t-bone");

        final Session unwrap = provider.em().unwrap(Session.class);

        final ProgrammingLanguage java = unwrap.bySimpleNaturalId(ProgrammingLanguage.class).load("java");

        Assert.assertNotNull(java);
    }

    @Test
    public void selectWithoutFetch() {
        List resultList = provider.em().createQuery("select c from ProgrammingLanguage c").getResultList();
        resultList.forEach(System.out::println);
    }

}