package io.costax.hibernatetuning.enhance.onetoone;

import io.costax.hibernatetuning.enhance.model.Cc;
import io.costax.hibernatetuning.enhance.model.HumanResource;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;

@JpaTest(persistenceUnit = "it")
public class OneToOneTest {

    @JpaContext
    public JpaProvider provider;

    /**
     *  Demonstration of the N+1 JPA problem
     */
    @Test
    @DisplayName("Demonstration of the N+1 JPA problem")
    public void selectWithoutFetch() {
        final EntityManager em = provider.em();

        List<Cc> resultList = em
                .createQuery("select c from Cc c", Cc.class)
                .getResultList();

        resultList.forEach(System.out::println);

        em.close();
    }

    @Test
    @DisplayName("Solution of the N+1 JPA problem, with left join fetch in the query")
    public void selectWithFetch() {

        final EntityManager em = provider.em();

        List<Cc> resultList = em
                .createQuery("select c from Cc c left join fetch c.document", Cc.class)
                .getResultList();

        resultList.forEach(System.out::println);

        em.close();
    }


    @Test
    public void getOne() {
        final EntityManager em = provider.em();

        Cc cc = em.find(Cc.class, 1);

        System.out.println(cc);

        em.close();
    }

    @Test
    public void shouldCreateOneRecord() {
        final EntityManager em = provider.em();
        em.getTransaction().begin();

        String userName = "duke - " + UUID.randomUUID();
        HumanResource rh = HumanResource.of(userName, "javaEE for jakarta");

        em.persist(rh);

        em.getTransaction().commit();

        em.getTransaction().begin();

        em.createQuery("delete HumanResource  where id = :id")
                .setParameter("id", rh.getId())
                .executeUpdate();

        em.getTransaction().commit();
    }
}
