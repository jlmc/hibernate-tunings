package io.costax.hibernatetuning.listener;

import io.costax.hibernatetuning.entities.Client;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HibernateEventListenerTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void a_createTwoClients() {

        provider.beginTransaction();

        provider.em().createNativeQuery("delete from client c").executeUpdate();
        provider.em().flush();

        final Client ptech = new Client(1, "Ptech", "Present-Technologies");
        provider.em().persist(ptech);

        final Client nasa = new Client(2, "Nasa", "Nasa");
        provider.em().persist(nasa);

        final Client google = new Client(3, "G", "Google");
        provider.em().persist(google);

        provider.commitTransaction();
    }

    @Test
    public void b_updateAExistingClient() {
        provider.beginTransaction();

        final Client nasa = provider.em().find(Client.class, 2);
        nasa.setName("National Aeronautics and Space Administration");

        provider.commitTransaction();
    }

    @Test
    public void c_deleteeAExistingClient() {
        provider.beginTransaction();

        final Client nasa = provider.em().getReference(Client.class, 2);
        provider.em().remove(nasa);

        provider.commitTransaction();
    }

    @Test
    public void d_deleteAexistingClientUsingNativeQuery() {
        final Number before = (Number) provider.em().createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        provider.beginTransaction();

        provider.em().createNativeQuery("delete from client where id = 1").executeUpdate();

        provider.commitTransaction();

        final Number after = (Number) provider.em().createNativeQuery("select count(t.id) from client_trace t").getSingleResult();


        System.out.println("---" + before);
        System.out.println("---" + after);
        Assert.assertThat(before.longValue(), is(not(Matchers.lessThan(after.longValue()))));
    }

    @Test
    public void e_ShouldDeleteUsingJpql() {
        final Number before = (Number) provider.em().createNativeQuery("select count(t.id) from client_trace t").getSingleResult();

        provider.beginTransaction();

        provider.em().createQuery("delete from Client where id = 3").executeUpdate();

        provider.commitTransaction();

        final Number after = (Number) provider.em().createNativeQuery("select count(t.id) from client_trace t").getSingleResult();


        System.out.println("---" + before);
        System.out.println("---" + after);
        Assert.assertThat(before.longValue(), is(not(Matchers.lessThan(after.longValue()))));
    }

    @Test
    public void z_dropAll() {
        provider.beginTransaction();

        provider.em().createNativeQuery("delete from client_trace").executeUpdate();
        provider.em().createNativeQuery("delete from client").executeUpdate();

        provider.commitTransaction();
    }
}