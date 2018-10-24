package io.costax.hibernatetunnig.overrideIdstrategy.overrideIdstrategy;

import io.costax.hibernatetunnig.overrideIdstrategy.entity.ProgrammingLanguage;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;


public class OverrideIdStrategyTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void createProgramingLanguages() {

        String programingLanguagesNames[] = {
                "java", "javascript", "csharp", "sql"
        };

        provider.beginTransaction();

        for (String pn : programingLanguagesNames) {
            ProgrammingLanguage programmingLanguage = ProgrammingLanguage.of(pn);
            provider.em().persist(programmingLanguage);
        }

        provider.commitTransaction();
    }

    @Test
    public void shoudlUseAsignedIds() {

        // python and skala

        provider.beginTransaction();

        provider.em().merge(ProgrammingLanguage.of(9001L, "python"));
        provider.em().merge(ProgrammingLanguage.of(9002L, "scala"));

        provider.commitTransaction();

    }

    @Test
    public void selectWithoutFetch() {


        List resultList = provider.em().createQuery("select c from ProgrammingLanguage c").getResultList();

        resultList.forEach(System.out::println);

    }

}