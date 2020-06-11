package io.costax.hibernatetunning.connections;

import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@JpaTest(persistenceUnit = "it")
public class ResourceLocalDelayConnectionAcquisitionTest {

    @PersistenceContext
    public EntityManager em;

    /**
     * The most interesting results can be found in the logger StatisticsReport.
     * That intends to show how to extends the hibernate.stats.factory default beaver!
     */
    @Test
    @DisplayName("Resource Local Delay Connection Acquisition, See the Logger to find the custom StatisticsReport")
    public void test() {

        em.getTransaction().begin();

        List<Developer> resultList = em
                .createQuery("select d from Developer d order by d.id ", Developer.class)
                .getResultList();

        resultList.forEach(System.out::println);

        em.getTransaction().commit();
    }
}
