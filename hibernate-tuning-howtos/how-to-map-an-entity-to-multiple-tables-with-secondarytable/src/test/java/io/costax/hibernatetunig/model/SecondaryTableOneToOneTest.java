package io.costax.hibernatetunig.model;

import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.annotation.Sql;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql(statements = {
        "delete from tasks.todo_details",
        "delete from tasks.todo",
}, phase = Sql.Phase.BEFORE_TEST_METHOD)
@Sql(statements = {
        "delete from tasks.todo_details",
        "delete from tasks.todo",
}, phase = Sql.Phase.AFTER_TEST_METHOD)
public class SecondaryTableOneToOneTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void should_persist_an_entity_in_two_tables() {

        provider.doInTx(em -> {
            final OffsetDateTime lisbonDate = OffsetDateTime.of(2018, 2, 10, 17, 20, 0, 0, ZoneOffset.UTC);

            final Todo slb = Todo.of(1L, "Win 10-Zero", lisbonDate, "SLB");

            em.persist(slb);
        });

        provider.doIt(em -> {
            final OffsetDateTime lisbonDate = OffsetDateTime.of(2018, 2, 10, 17, 20, 0, 0, ZoneOffset.UTC);
            final Todo expected = Todo.of(1L, "Win 10-Zero", lisbonDate, "SLB");

            final Todo todo = em.find(Todo.class, 1L);

            Assertions.assertNotNull(todo);
        });

        provider.doInTx(em -> {
            final Todo todo = em.getReference(Todo.class, 1L);
            em.remove(todo);
        });
    }

}
