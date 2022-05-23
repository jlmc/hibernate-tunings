package io.costax.hibernatetuning.storedprocedures;

import io.costax.hibernatetunning.entities.Project;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoredProcedureTest {

    @JpaContext
    public JpaProvider provider;

    @BeforeEach
    public void populate() {

        provider.doInTx(em -> {
            for (int i = 0; i < 2; i++) {
                Project p = new Project("Lighthouse-" + i);
                provider.em().persist(p);
            }
        });

    }

    @AfterEach
    public void cleanUp() {
        // using criteria to create a delete query, just because we can and for fun..
        provider.doInTx(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            // create delete
            CriteriaDelete<Project> delete = cb.createCriteriaDelete(Project.class);

            // set the root class
            final Root<Project> e = delete.from(Project.class);

            // set where clause
            delete.where(cb.greaterThan(e.get("id"), 0L));

            // perform update
            em.createQuery(delete).executeUpdate();
        });
    }

    @Test
    @Order(0)
    public void update_using_criteria_just_for_fun() {
        provider.doInTx(em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();

            // create update
            CriteriaUpdate<Project> update = cb.createCriteriaUpdate(Project.class);

            // set the root class
            final Root<Project> root = update.from(Project.class);

            // set update and where clause
            final String oldTitle = "Lighthouse-1";
            final String newTitle = "Vanilla";

            update.set("title", newTitle);
            update.where(cb.equal(root.get("title"), oldTitle));

            // perform update
            em.createQuery(update).executeUpdate();
        });

    }

    @Test
    @Order(1)
    public void call_simple_stored_procedure() {
        final Integer invocationResult = provider.doItWithReturn(em -> {


            final StoredProcedureQuery increment = em.createStoredProcedureQuery("increment");

            /*
             * Parameter modes
             *
             * IN: for input parameters,
             * OUT: for output parameters,
             * INOUT: for parameters which are used for input and output and
             * REF_CURSOR: for cursors on a result set.
             */
            increment.registerStoredProcedureParameter("i", Integer.class, ParameterMode.IN);
            increment.registerStoredProcedureParameter("x", Integer.class, ParameterMode.OUT);

            increment.setParameter("i", 5);

            final boolean execute = increment.execute();

            return (Integer) increment.getOutputParameterValue("x");
        });

        assertEquals(6, invocationResult);
    }


    @Test
    @Order(2)
    public void call_simple_stored_procedureInOut() {
        final Integer invocationResult = provider.doItWithReturn(em -> {

            final StoredProcedureQuery increment = em.createStoredProcedureQuery("increment");

            /*
             * Parameter modes
             *
             * IN: for input parameters,
             * OUT: for output parameters,
             * INOUT: for parameters which are used for input and output and
             * REF_CURSOR: for cursors on a result set.
             */
            increment.registerStoredProcedureParameter("i", Integer.class, ParameterMode.INOUT);
            //increment.registerStoredProcedureParameter("x", Integer.class, ParameterMode.OUT);

            increment.setParameter("i", 5);

            final boolean execute = increment.execute();

            return (Integer) increment.getOutputParameterValue("i");
        });

        assertEquals(6, invocationResult);
    }


    /**
     * Because the our examples stored procedures are functions, we have to open transactions
     */
    @Test
    @Order(3)
    @DisplayName("call stored procedures that create records, an Active transaction is necessary")
    public void call_stored_procedures_that_create_records() {
        provider.doInTxWithReturn(em -> {

            final StoredProcedureQuery addProject = em.createStoredProcedureQuery("add_project");
            addProject.registerStoredProcedureParameter("ptitle", String.class, ParameterMode.IN);
            addProject.registerStoredProcedureParameter("id", Integer.class, ParameterMode.OUT);
            addProject.setParameter("ptitle", "Dummy project");

            addProject.execute();
            //addProject.executeUpdate();

            return (Integer) addProject.getOutputParameterValue("id");
        });

        Long count = (Long)
                provider.em()
                        .createNativeQuery("select count(id) from project", Long.class)
                        .getSingleResult();
        assertEquals(1L, count.longValue());
    }
}
