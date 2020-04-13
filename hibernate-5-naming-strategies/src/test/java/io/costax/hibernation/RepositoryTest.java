package io.costax.hibernation;

import io.costax.hibernation.model.ShoppingList;
import io.costax.hibernation.model.ShoppingList_;
import io.costax.hibernation.model.Topic;
import io.costax.hibernation.model.Topic_;
import io.costax.persistence.api.PersistenceExtension;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.persistence.criteria.*;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.costax.persistence.api.ThreadLocalEntityManagerProvider.doIt;
import static io.costax.persistence.api.ThreadLocalEntityManagerProvider.doItTx;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ExtendWith(PersistenceExtension.class)
class RepositoryTest {

    @RegisterExtension
    static PersistenceExtension persistenceExtension = PersistenceExtension.withPersistenceUnit("it");

    @Test
    @Order(1)
    void shouldExecuteJPQLQuery() {
        // 2020-04-11 13:05:02.000000
        final ZonedDateTime zonedDateTime = LocalDate.of(2020, Month.APRIL, 11)
                .atTime(LocalTime.of(13, 5, 1, 123_456_789))
                .atZone(ZoneId.of("Europe/Lisbon"));

        List<ShoppingList> result = doIt(em -> {
            return em.createQuery("""
                            select distinct m 
                            from ShoppingList m 
                            left join fetch m.topics t 
                            where m.createAt >= :time
                            order by m.id ,t.id 
                            """
                    , ShoppingList.class)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .setParameter("time", zonedDateTime.toOffsetDateTime())
                    .getResultList();
        });

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(2, result.get(0).getTopics().size());
    }

    @Test
    @Order(1)
    void shouldExecuteCriteriaQuery() {
        final long shoppingListId = 1L;

        List<Topic> results = doIt(em -> {

            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Topic> query = builder.createQuery(Topic.class);
            Root<Topic> topics = query.from(Topic.class);

            List<Predicate> predicates = new ArrayList<>();
            {
                Subquery<Long> subQuery = query.subquery(Long.class);
                Root<ShoppingList> shoppingListRoot = subQuery.from(ShoppingList.class);
                ListJoin<ShoppingList, Topic> topics1 = shoppingListRoot.join(ShoppingList_.topics);
                subQuery.select(topics1.get(Topic_.id))
                        .where(builder.equal(shoppingListRoot.get(ShoppingList_.id), builder.literal(shoppingListId)));
                predicates.add(builder.in(topics.get(Topic_.id)).value(subQuery));
            }

            query.distinct(true)
                    .where(predicates.toArray(new Predicate[0]))
                    .orderBy(builder.asc(topics.get(Topic_.numberOfItems)));

            return em.createQuery(query).getResultList();
        });

        System.out.println("Should the result");
        results.forEach(System.out::println);

        assertEquals(2, results.size());
    }

    @Test
    @Order(3)
    void shouldAddMoreTopics() {
        final long primaryKey = 1L;
        doItTx(em -> {
            ShoppingList shoppingList = em.find(ShoppingList.class, primaryKey);

            shoppingList
                    .getTopics()
                    .stream()
                    .min(Comparator.comparing(Topic::getId, nullsLast(naturalOrder())))
                    .ifPresent(shoppingList::removeTopic);

            shoppingList.addTopic("Torradeira", "Com pouca potência e barata", 1);
            shoppingList.addTopic("Carne Picada", "1.5kg para lasanha", 1);
            shoppingList.addTopic("Popa tomate", "", 2);
        });


        List<Topic> topics = doIt(em -> {
            ShoppingList shoppingList = em.find(ShoppingList.class, primaryKey);
            return shoppingList.getTopics();
        });

        assertEquals(4L, topics.size(), "Initially the collection had 2 elements, but in the transaction we added 3 more elements, but removed 1! So the collection is expected to have 4 elements");

    }
}