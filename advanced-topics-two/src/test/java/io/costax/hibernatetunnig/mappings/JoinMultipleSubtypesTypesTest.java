package io.costax.hibernatetunnig.mappings;

import io.costax.hibernatetunnig.entities.Bicycle;
import io.costax.hibernatetunnig.entities.Car;
import io.costax.hibernatetunnig.entities.Garage;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Objects;

public class JoinMultipleSubtypesTypesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

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

        final Garage elephant = provider.em().find(Garage.class, 1);
        final List<Car> cars = elephant.getCars();
        final List<Bicycle> bicycles = elephant.getBicycles();

        Assert.assertThat(elephant.getVehicles(), Matchers.hasSize(4));
        Assert.assertThat(cars, Matchers.hasSize(2));
        Assert.assertNotNull(cars.stream().filter(p -> Objects.equals(1, p.getId())).findAny().orElse(null) );
        Assert.assertNotNull(cars.stream().filter(p -> Objects.equals(2, p.getId())).findAny().orElse(null) );


        Assert.assertThat(bicycles, Matchers.hasSize(2));
        Assert.assertNotNull(bicycles.stream().filter(p -> Objects.equals(3, p.getId())).findAny().orElse(null) );
        Assert.assertNotNull(bicycles.stream().filter(p -> Objects.equals(8, p.getId())).findAny().orElse(null) );

        provider.doInTx(em -> {
            final Garage elephant1 = em.find(Garage.class, 1);
            final Garage zebra1 = em.find(Garage.class, 2);

            final Bicycle bicycle = zebra1.getBicycles().get(0);
            elephant1.addBicycle(bicycle);

            em.flush();

        });

        provider.em().refresh(elephant);

        Assert.assertThat(elephant.getVehicles(), Matchers.hasSize(5));
        Assert.assertThat(elephant.getBicycles(), Matchers.hasSize(3));
    }
}
