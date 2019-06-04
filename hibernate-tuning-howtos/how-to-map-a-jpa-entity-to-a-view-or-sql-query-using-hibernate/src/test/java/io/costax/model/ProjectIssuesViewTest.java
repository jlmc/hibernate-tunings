package io.costax.model;

import io.costax.rules.EntityManagerProvider;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

public class ProjectIssuesViewTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectIssuesViewTest.class);

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void should_get_result_from_a_view() {
        final List<ProjectIssuesView> result = provider.em()
                .createQuery("select v from ProjectIssuesView v", ProjectIssuesView.class)
                .getResultList();

        Assert.assertNotNull(result);
    }

    @Test
    public void should_get_result_from_subselect() {
        List<DatabaseFunction> databaseFunctions =
                provider.em().createQuery(
                        "select df " +
                                "from DatabaseFunction df", DatabaseFunction.class)
                        .getResultList();

        Assert.assertNotNull(databaseFunctions);

    }
}