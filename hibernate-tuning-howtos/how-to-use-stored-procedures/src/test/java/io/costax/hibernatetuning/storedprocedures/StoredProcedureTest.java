package io.costax.hibernatetuning.storedprocedures;

import io.costax.hibernatetunning.entities.Project;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.junit.*;
import org.junit.runners.MethodSorters;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StoredProcedureTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Before
    public void populate() {
        provider.beginTransaction();

        for (int i = 0; i < 2; i++) {
            Project p = new Project("Lighthouse-" + i);
            provider.em().persist(p);
        }

        provider.commitTransaction();
    }

    @After
    public void cleanUp() {
        // using criteria to create a delete query, just because we can and for fun..
        provider.beginTransaction();

        final EntityManager em = provider.em();

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // create delete
        CriteriaDelete<Project> delete = cb.createCriteriaDelete(Project.class);

        // set the root class
        final Root<Project> e = delete.from(Project.class);

        // set where clause
        delete.where(cb.greaterThan(e.get("id"), 0L));

        // perform update
        em.createQuery(delete).executeUpdate();

        provider.commitTransaction();
    }

    @Test
    public void t00_update_using_criteria_just_for_fun() {
        provider.beginTransaction();
        final EntityManager em = provider.em();
        CriteriaBuilder cb = em.getCriteriaBuilder();

        // create update
        CriteriaUpdate<Project> update = cb.createCriteriaUpdate(Project.class);

        // set the root class
        Root e = update.from(Project.class);

        // set update and where clause
        final String oldTitle = "Lighthouse-1";
        final String newTitle = "Vanilla";

        update.set("title", newTitle);
        update.where(cb.equal(e.get("title"), oldTitle));

        // perform update
        em.createQuery(update).executeUpdate();

        provider.commitTransaction();
    }

    @Test
    public void t00_call_simple_stored_procedure() {
        final EntityManager em = provider.em();

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

        Integer result = (Integer) increment.getOutputParameterValue("x");

        System.out.println(result);
    }


    @Test
    public void t00_call_simple_stored_procedureInOut() {
        final EntityManager em = provider.em();

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

        Integer result = (Integer) increment.getOutputParameterValue("i");

        System.out.println(result);
    }


    /**
     * Because the our examples stored procedures are functions, we have to open transactions
     */
    @Test
    public void t01_callCreate() {
        provider.beginTransaction();
        final EntityManager em = provider.em();

        final StoredProcedureQuery addProject = em.createStoredProcedureQuery("add_project");
        addProject.registerStoredProcedureParameter("ptitle", String.class, ParameterMode.IN);
        addProject.registerStoredProcedureParameter("id", Integer.class, ParameterMode.OUT);
        addProject.setParameter("ptitle", "Dummy project");

        addProject.execute();
        //addProject.executeUpdate();

        final Integer result = (Integer) addProject.getOutputParameterValue("id");

        //em.flush();
        provider.commitTransaction();

        final BigInteger count = (BigInteger) provider.em().createNativeQuery("select count(id) from project").getSingleResult();

        Assert.assertEquals(3L, count.longValue());
    }

    /**
     * To Use a Cursor we need to have a active transaction or declare the connection as connection.setAutoCommit(false)
     */
    @Test
    public void t02_fetch_projects_using_cursor() throws SQLException {

        final EntityManager em = provider.createdEntityManagerUnRuled();
        //em.getTransaction().begin();


        // Mark the connection as AutoCommit = false
        final Session session = em.unwrap(Session.class);
        SharedSessionContractImplementor sharedSessionContractImplementor = (SharedSessionContractImplementor) session;
        Connection connection = sharedSessionContractImplementor.connection();
        connection.setAutoCommit(false);

        /*
            Session session = em.unwrap(Session.class);
            session.doWork(new Work() {
            @Override
                public void execute(Connection connection) throws SQLException {
                // do whatever you need to do with the connection
                connection.setAutoCommit(false);
             }
             });
        */

        StoredProcedureQuery q = em.createNamedStoredProcedureQuery("getProjects");
        q.setParameter(2, "Lighthouse%");
        List<Project> projects = q.getResultList();

        for (Project b : projects) {
            System.out.println("\n >>> " + b.getId() + " ---" + b.getTitle());
        }

        Assert.assertEquals(2, projects.size());

        //em.getTransaction().commit();
    }
}
