package io.costax.hibernation;

import io.costax.hibernation.model.ShoppingList;
import io.costax.hibernation.model.ShoppingList_;
import io.costax.hibernation.model.Topic;
import io.costax.hibernation.model.Topic_;
import io.github.jlmc.jpa.test.annotation.JpaContext;
import io.github.jlmc.jpa.test.annotation.JpaTest;
import io.github.jlmc.jpa.test.junit.JpaProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JpaTest(persistenceUnit = "it")
class RepositoryTest {

    @JpaContext
    JpaProvider provider;

    @Test
    @Order(1)
    void shouldExecuteJPQLQuery() {
        // 2020-04-11 13:05:02.000000
        final ZonedDateTime zonedDateTime = LocalDate.of(2020, Month.APRIL, 11)
                                                     .atTime(LocalTime.of(13, 5, 1, 123_456_789))
                                                     .atZone(ZoneId.of("Europe/Lisbon"));

        List<ShoppingList> result = provider.doItWithReturn(em ->
                em.createQuery("""
                                  select distinct m
                                  from ShoppingList m
                                  left join fetch m.topics t
                                  where m.createAt >= :time
                                  order by m.id ,t.id
                                  """
                          , ShoppingList.class)

                  .setHint("hibernate.query.passDistinctThrough", false)

                  .setParameter("time", zonedDateTime.toOffsetDateTime())
                  .getResultList());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0));
        assertEquals(2, result.get(0).getTopics().size());
    }

    @Test
    @Order(1)
    void shouldExecuteCriteriaQuery() {
        final long shoppingListId = 1001L;

        List<Topic> results = provider.doItWithReturn(em -> {

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
        final long primaryKey = 1001L;
        provider.doInTx(em -> {
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


        List<Topic> topics = provider.doItWithReturn(em -> {
            ShoppingList shoppingList = em.find(ShoppingList.class, primaryKey);
            return shoppingList.getTopics();
        });

        assertEquals(4L, topics.size(), "Initially the collection had 2 elements, but in the transaction we added 3 more elements, but removed 1! So the collection is expected to have 4 elements");

    }
}
