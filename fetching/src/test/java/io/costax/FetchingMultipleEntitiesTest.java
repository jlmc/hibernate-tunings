package io.costax;

import io.costax.model.Project;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@JpaTest(persistenceUnit = "it")
public class FetchingMultipleEntitiesTest {

    @PersistenceContext
    public EntityManager em;

    @Test
    public void fetchMultipleEntities() {

        final List<Project> projects = em
                .createQuery("select p from Project p where p.id in ( :id )", Project.class)
                .setParameter("id", List.of(1L, 2L, 3L))
                .getResultList();

        projects.forEach(System.out::println);
    }
}
