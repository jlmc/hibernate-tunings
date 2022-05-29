package io.github.jlmc.types.custom;

import io.github.jlmc.entities.User;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.Stream;

@JpaTest(persistenceUnit = "it")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class PostgresArrayTypesTest {

    private static final Long ID = 1L;

    @JpaContext
    JpaProvider jpa;

    @Test
    void postgresTextArray() {

        jpa.doInTx(em -> {
            System.out.println("Persist a new Entity with all arrays defined");

            User user = new User();
            user.setId(ID);

            user.setRoles(new String[]{"agent", "supervisor"});

            user.setNumber(new int[]{1, 2, 3, 4, 5, 6});

            user.setFlags(new boolean[] {true, false, true});

            em.persist(user);
        });

        jpa.doIt(em -> {
            System.out.println("Read a entity with arrays types");

            User user = em.find(User.class, ID);

            System.out.println(" ------\n " + user + "\n ------");
        });


        jpa.doInTx(em -> {
            em.clear();

            System.out.println("Update a entity with arrays types");

            User user = em.find(User.class, ID);

            String[] newRoles =
                    Stream.concat(
                                  Arrays.stream(user.getRoles()),
                                  Stream.of("Administrator", "common-human-being", "agent"))
                          .map(String::toLowerCase)
                          .distinct().toArray(String[]::new);

            user.setRoles(newRoles);

            int[] newNumbers = Arrays.stream(user.getNumber()).map(i -> i * 3).toArray();
            user.setNumber(newNumbers);

        });

        jpa.doInTx(em -> {
            em.clear();

            System.out.println("Update a single index of the array");

            User user = em.find(User.class, ID);


            user.getNumber()[0] = 999;

            em.flush();

        });
    }
}
