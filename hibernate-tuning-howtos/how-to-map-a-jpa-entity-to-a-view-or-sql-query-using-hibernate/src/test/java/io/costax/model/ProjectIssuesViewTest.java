package io.costax.model;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ProjectIssuesViewTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIssuesViewTest.class);

    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_get_result_from_a_view() {
        final List<ProjectIssuesView> result = provider
                .doItWithReturn(em ->
                        em.createQuery("select v from ProjectIssuesView v", ProjectIssuesView.class).getResultList());

        assertNotNull(result);
    }

    @Test
    public void should_get_result_from_sub_select() {
        List<DatabaseFunction> databaseFunctions =
                provider.doItWithReturn(em -> {

                    return em.createQuery(
                            "select df " +
                                    "from DatabaseFunction df", DatabaseFunction.class)
                            .getResultList();
                });


        assertNotNull(databaseFunctions);
    }
}