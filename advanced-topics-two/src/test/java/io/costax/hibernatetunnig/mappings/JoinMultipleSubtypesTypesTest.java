package io.costax.hibernatetunnig.mappings;

import io.costax.hibernatetunnig.entities.Bicycle;
import io.costax.hibernatetunnig.entities.Car;
import io.costax.hibernatetunnig.entities.Garage;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest(persistenceUnit = "it")
public class JoinMultipleSubtypesTypesTest {

    @JpaContext
    public JpaProvider provider;

    @Test
    public void mapping() {
        provider.doInTx(em -> {
            final Garage elephant = Garage.of(1, "Elephant");
            elephant.add(Car.of(1, "Ricardo", "Astra"));
            elephant.add(Car.of(2, "Fabio", "Mercedes Bez"));
            elephant.add(Bicycle.of(3, "Joana", "27.50"));
            elephant.add(Bicycle.of(8, "Joao", "29.00"));
            em.persist(elephant);

            final Garage zebra = Garage.of(2, "Zebra");
            zebra.add(Car.of(4, "Marcelo", "Peugeot 206"));
            zebra.add(Bicycle.of(5, "Diogo", "26.00"));
            em.persist(zebra);

            em.flush();
        });


        Garage elephant = provider.em().find(Garage.class, 1);
        final List<Car> cars = elephant.getCars();
        final List<Bicycle> bicycles = elephant.getBicycles();
        assertThat(elephant.getVehicles()).hasSize(4);
        assertThat(cars).hasSize(2);
        assertThat(cars.stream().filter(p -> Objects.equals(1, p.getId())).findAny().orElse(null)).isNotNull();
        assertThat(cars.stream().filter(p -> Objects.equals(2, p.getId())).findAny().orElse(null)).isNotNull();
        assertThat(bicycles).hasSize(2);
        assertThat(bicycles.stream().filter(p -> Objects.equals(3, p.getId())).findAny().orElse(null)).isNotNull();
        assertThat(bicycles.stream().filter(p -> Objects.equals(8, p.getId())).findAny().orElse(null)).isNotNull();

        provider.doInTx(em -> {
            final Garage elephant1 = em.find(Garage.class, 1);
            final Garage zebra1 = em.find(Garage.class, 2);

            final Bicycle bicycle = zebra1.getBicycles().get(0);
            elephant1.addBicycle(bicycle);
            em.flush();
        });


        final EntityManager em = provider.em();

        elephant = em.find(Garage.class, 1);

        assertThat(elephant.getVehicles()).hasSize(5);
        assertThat(elephant.getBicycles()).hasSize(3);

        em.close();
    }
}
