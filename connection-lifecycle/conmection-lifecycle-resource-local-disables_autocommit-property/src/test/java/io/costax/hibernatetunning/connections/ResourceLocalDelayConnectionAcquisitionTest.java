package io.costax.hibernatetunning.connections;

import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class ResourceLocalDelayConnectionAcquisitionTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void test() {

        // we should swith on and o

        provider.beginTransaction();

        List<Developer> resultList = provider.em().createQuery("select d from Developer d order by d.id ", Developer.class).getResultList();

        resultList.stream().forEach(System.out::println);

        provider.commitTransaction();
    }
}
