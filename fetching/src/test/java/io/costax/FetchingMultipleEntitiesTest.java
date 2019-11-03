package io.costax;

import io.costax.model.Project;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

public class FetchingMultipleEntitiesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void fetchMultipleEntities() {

        final EntityManager em = provider.em();

        final List<Project> projects = em.createQuery("select p from Project p where p.id in ( :id )", Project.class)
                .setParameter("id", List.of(1L, 2L, 3L))
                .getResultList();

        projects.stream().forEach(System.out::println);
    }
}
