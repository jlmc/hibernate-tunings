package io.costax.hibernatetuning.enhance.onetoone;

import io.costax.hibernatetuning.enhance.model.Cc;
import io.costax.hibernatetuning.enhance.model.HumanResource;
import io.costax.rules.EntityManagerProvider;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

public class OneToOneTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void selectWithoutFetch() {
        /**
         * demonstração do problema N + 1
         */

        List resultList = provider.em().createQuery("select c from Cc c").getResultList();

        resultList.forEach(System.out::println);

    }

    @Test
    public void selectWithtFetch() {
        /**
         * Com fetch o N+1 é ignorado
         */

        List resultList = provider.em().createQuery("select c from Cc c join fetch c.document").getResultList();

        resultList.forEach(System.out::println);
    }


    @Test
    public void shoulGetOne() {
        Cc cc = provider.em().find(Cc.class, 1);

        System.out.println(cc);
    }

    @Test
    public void shouldCreateOneRecord() {
        provider.beginTransaction();

        String userName = "duke - " + UUID.randomUUID();

        HumanResource rh = HumanResource.of(userName, "javaEE for jakarta");

        provider.em().persist(rh);

        provider.commitTransaction();

        provider.beginTransaction();

        provider.em().createQuery("delete HumanResource  where id = :id")
                .setParameter("id", rh.getId())
                .executeUpdate();

        provider.commitTransaction();
    }
}
