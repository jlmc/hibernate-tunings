package io.costax.bootstrap_jpa_programmatically;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

public class ExampleBootstrapTest extends AbstractJPAProgrammaticBootstrapTest {

    @BeforeAll
    static void beforeAll() {
        //  <property name="hibernate.format_sql" value="true"/>
        System.setProperty("hibernate.format_sql", "true");
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[] {
                Person.class
        };
    }

    @Test
    @Order(1)
    void createRecord() {
        final EntityManagerFactory emf = super.entityManagerFactory();

        EntityManager em = null;
        EntityTransaction tx = null;


        try {

            em = emf.createEntityManager();
            tx = em.getTransaction();

            tx.begin();


            final Person gama = new Person("Vasco da Gama");
            em.persist(gama);


            tx.commit();

        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }


    }


    @Test
    @Order(2)
    void getAllRecords() {
        final EntityManagerFactory emf = super.entityManagerFactory();

        final EntityManager entityManager = emf.createEntityManager();

        //final List resultList = entityManager.createQuery("select p from Person p", Person.class).getResultList();
        final Person person = entityManager.find(Person.class, 1);

        entityManager.close();
    }

}


@Table(name = "Person")
@Entity(name = "Person")
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    protected Person() {
    }

    public Person(final String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
