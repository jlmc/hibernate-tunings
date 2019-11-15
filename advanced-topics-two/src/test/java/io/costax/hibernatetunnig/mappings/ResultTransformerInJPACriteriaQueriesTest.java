package io.costax.hibernatetunnig.mappings;

import io.costax.hibernatetunnig.entities.Developer;
import io.costax.hibernatetunnig.entities.Developer_;
import io.costax.hibernatetunnig.entities.Machine;
import io.costax.hibernatetunnig.entities.Machine_;
import io.costax.hibernatetunnig.transformers.FactoryMethodTransformerAdapter;
import io.costax.rules.EntityManagerProvider;
import org.hamcrest.Matchers;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class ResultTransformerInJPACriteriaQueriesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void setMultiselectInCriteria() {
        final EntityManager em = provider.em();

        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery query = builder.createQuery();
        final Root<Machine> machineRoot = query.from(Machine.class);
        final Join<Machine, Developer> developer = machineRoot.join("developer");

        //@formatter:off
        List<SpotDeveloperMachine> results =
                em.createQuery(
                            query.multiselect(
                                machineRoot,
                                developer.get("name"),
                                builder.count(machineRoot.get("id")))
                            .groupBy(machineRoot)
                            .orderBy(builder.asc(machineRoot.get("id"))))
                        .unwrap(Query.class)
                        .setResultTransformer(FactoryMethodTransformerAdapter.of(SpotDeveloperMachine.class, "of"))
                        .getResultList();
        //@formatter:on

        Assert.assertNotNull(results);
        assertThat(results, Matchers.hasSize(4));
        final SpotDeveloperMachine firstItem = results.get(0);
        final SpotDeveloperMachine lastItem = results.stream().reduce((first, second) -> second).orElse(null);
        assertThat(firstItem, notNullValue());
        assertThat(lastItem, notNullValue());
        assertThat(firstItem.getScalarValue(), is(1L));
        assertThat(lastItem.scalarValue, is(1L));
        assertThat(firstItem.getDescription(), is("Spot of 'Ricardo' using the Machine [1 - mac] --> 1 Systems"));
        assertThat(lastItem.getDescription(), is("Spot of 'Joana' using the Machine [4 - Asus] --> 1 Systems"));
    }

    @Test
    public void setResultTransformerInCriteria() {
        final EntityManager em = provider.em();

        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery query = builder.createQuery();
        final Root<Machine> machineRoot = query.from(Machine.class);
        final Join<Machine, Developer> developer = machineRoot.join("developer");

        //@formatter:off
        List<Dto> results =
                em.createQuery(
                        query.select(
                            builder.tuple(
                                    machineRoot.get(Machine_.brand),
                                    developer.get(Developer_.name)))
                        .orderBy(builder.asc(machineRoot.get("id"))))
                    .unwrap(Query.class)
                    .setResultTransformer(FactoryMethodTransformerAdapter.of(Dto.class, "of"))
                    .getResultList();
        //@formatter:on

        Assert.assertNotNull(results);
        Assert.assertThat(results, containsInAnyOrder(
                samePropertyValuesAs(Dto.of("mac", "Ricardo")),
                samePropertyValuesAs(Dto.of("Lenovo", "Fabio")),
                samePropertyValuesAs(Dto.of("Asus", "Ricardo")),
                samePropertyValuesAs(Dto.of("mac", "Joana"))
        ));
    }

    private static class Dto {
        private final String machineBrand;
        private final String developerName;

        private Dto(final String machineBrand, final String developerName) {
            this.machineBrand = machineBrand;
            this.developerName = developerName;
        }

        public static Dto of(final String machineBrand, final String developerName) {
            return new Dto(machineBrand, developerName);
        }
    }

    private static class SpotDeveloperMachine {
        private final Machine machine;
        private final String ownerName;
        private final Long scalarValue;
        private final String description;

        private SpotDeveloperMachine(final Machine machine, final String ownerName, final Long scalarValue, final String description) {
            this.machine = machine;
            this.ownerName = ownerName;
            this.scalarValue = scalarValue;
            this.description = description;
        }

        public static SpotDeveloperMachine of(final Machine machine, final String ownerName, final Long excalarValue) {
            final String description = String.format("Spot of '%s' using the Machine [%d - %s] --> %d Systems", ownerName, machine.getId(), machine.getBrand(), excalarValue);
            return new SpotDeveloperMachine(machine, ownerName, excalarValue, description);
        }

        public Machine getMachine() {
            return machine;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public Long getScalarValue() {
            return scalarValue;
        }

        public String getDescription() {
            return description;
        }
    }
}
