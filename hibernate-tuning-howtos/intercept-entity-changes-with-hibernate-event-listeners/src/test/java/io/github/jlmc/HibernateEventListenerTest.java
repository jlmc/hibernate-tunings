package io.github.jlmc;

import io.github.jlmc.entities.Tooy;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
public class HibernateEventListenerTest {

    @JpaContext
    JpaProvider jpa;

    @BeforeEach
    void before() {
        jpa.doInTx(em -> {
            em.createNativeQuery("delete from Tooy").executeUpdate();
            em.createNativeQuery("insert into Tooy (id, version, title) values (101, 1, 'Teddy')").executeUpdate();
            em.createNativeQuery("insert into Tooy (id, version, title) values (102, 1, 'Rubik cube')").executeUpdate();
            em.createNativeQuery("insert into Tooy (id, version, title) values (103, 1, 'Puzzle box')").executeUpdate();
        });
    }

    /**
     * Rigorous Test :-)
     */
    @Test
    @DisplayName("when persist a entity it should create a trace replica")
    public void when_persist_a_entity_should_be_create_a_trace_replica() {
        jpa.doInTx(em -> {
            Tooy ball = new Tooy(1L, "Ball");
            em.persist(ball);
            em.flush();
        });
    }

    @Test
    @DisplayName("when update a entity it should create a trace replica")
    public void when_update_a_entity_should_be_create_a_trace_replica() {
        jpa.doInTx(em -> {
            Tooy tooy = em.find(Tooy.class, 101L);
            tooy.setTitle("Teddy loving");
            em.flush();
        });
    }

    @Test
    @DisplayName("when delete a entity it should create a trace replica")
    public void when_delete_a_entity_should_be_create_a_trace_replica() {
        jpa.doInTx(em -> {
            Tooy tooy = em.find(Tooy.class, 102L);

            em.remove(tooy);
        });
    }
}
