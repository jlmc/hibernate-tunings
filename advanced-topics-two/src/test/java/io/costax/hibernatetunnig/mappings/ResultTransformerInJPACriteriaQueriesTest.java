package io.costax.hibernatetunnig.mappings;

import io.costax.hibernatetunnig.entities.Developer;
import io.costax.hibernatetunnig.entities.Machine;
import io.costax.hibernatetunnig.transformers.FactoryMethodTransformerAdapter;
import io.costax.rules.EntityManagerProvider;
import org.hibernate.query.Query;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.List;

public class ResultTransformerInJPACriteriaQueriesTest {

    @Rule
    public EntityManagerProvider provider = EntityManagerProvider.withPersistenceUnit("it");

    @Test
    public void setMultiselectInCriteria() {

        final EntityManager em = provider.em();

        final CriteriaBuilder builder = em.getCriteriaBuilder();

        final CriteriaQuery query = builder.createQuery(Object.class);
        final Root<Machine> machineRoot = query.from(Machine.class);
        final Join<Machine, Developer> developer = machineRoot.join("developer");

        final CriteriaQuery dtoCriteriaQuery = query.multiselect(
                machineRoot,
                developer.get("name"),
                builder.count(machineRoot.get("id")))
                .groupBy(machineRoot)
                .orderBy(builder.asc(machineRoot.get("id")));

        List<DtoMachineWithOnerName> resultList = em.createQuery(dtoCriteriaQuery)
                .unwrap(Query.class)
                .setResultTransformer(FactoryMethodTransformerAdapter.of(DtoMachineWithOnerName.class, "of"))
                .getResultList();

        Assert.assertNotNull(resultList);
    }

    @Test
    public void setResultTransformerInCriteria() {

        final EntityManager em = provider.em();

        final CriteriaBuilder builder = em.getCriteriaBuilder();

        final CriteriaQuery<Object> query = builder.createQuery(Object.class);
        final Root<Machine> machineRoot = query.from(Machine.class);
        final Join<Machine, Developer> developer = machineRoot.join("developer");

        final CriteriaQuery<Object> dtoCriteriaQuery = query.select(builder.tuple(machineRoot.get("id"), developer.get("name")))
                .orderBy(builder.asc(machineRoot.get("id")));


        final TypedQuery<Object> typedQuery = em.createQuery(dtoCriteriaQuery);

        typedQuery.unwrap(Query.class)
                .setResultTransformer(FactoryMethodTransformerAdapter.of(Dto.class, "of"));


        final List<Object> resultList = typedQuery.getResultList();

        Assert.assertNotNull(resultList);
    }


    private static class Dto {

        private Integer id;
        private String name;

        public Dto(final Integer id, final String name) {
            this.id = id;
            this.name = name;
        }

        public static Dto of(final Integer id, final String name) {
            return new Dto(id, name);
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    private static class DtoMachineWithOnerName {

        private final Machine machine;
        private final String ownerName;
        private final Long excalarValue;

        public DtoMachineWithOnerName(final Machine machine, final String ownerName, final Long excalarValue) {
            this.machine = machine;
            this.ownerName = ownerName;
            this.excalarValue = excalarValue;
        }

        public static DtoMachineWithOnerName of(final Machine machine, final String ownerName, final Long excalarValue) {

            // TODO: 15/11/2019 where we can do whatever we want

            return new DtoMachineWithOnerName(machine, ownerName, excalarValue);
        }
    }
}
