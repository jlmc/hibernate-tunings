package io.costax.hibernatetunnig.mappings;

import io.costax.hibernatetunnig.entities.Developer;
import io.costax.hibernatetunnig.entities.Developer_;
import io.costax.hibernatetunnig.entities.Machine;
import io.costax.hibernatetunnig.entities.Machine_;
import io.costax.hibernatetunnig.transformers.FactoryMethodTransformerAdapter;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import org.assertj.core.api.Assertions;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@JpaTest(persistenceUnit = "it")
public class ResultTransformerInJPACriteriaQueriesTest {

    @JpaContext
    public JpaProvider provider;

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


        assertThat(results)
                .isNotNull()
                .isNotEmpty()
                .hasSize(4);

        final SpotDeveloperMachine firstItem = results.get(0);
        final SpotDeveloperMachine lastItem = results.stream().reduce((first, second) -> second).orElse(null);


        assertThat(firstItem)
                .isNotNull();
        assertThat(firstItem.getScalarValue()).isEqualTo(1L);
        assertThat(lastItem)
                .isNotNull();
        assertThat(firstItem.getScalarValue()).
                isEqualTo(1L);
        assertThat(firstItem.getDescription()).isEqualTo("Spot of 'Ricardo' using the Machine [1 - mac] --> 1 Systems");
        assertThat(lastItem.getDescription()).isEqualTo("Spot of 'Joana' using the Machine [4 - mac] --> 1 Systems");
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

        Assertions.assertThat(results).isNotNull().isNotEmpty();

        Assertions.assertThat(results)
                .contains(
                        Dto.of("mac", "Ricardo"),
                        Dto.of("Lenovo", "Fabio"),
                        Dto.of("Asus", "Ricardo"),
                        Dto.of("mac", "Joana")
                );

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

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Dto dto = (Dto) o;
            return Objects.equals(machineBrand, dto.machineBrand) &&
                    Objects.equals(developerName, dto.developerName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(machineBrand, developerName);
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
