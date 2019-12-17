package io.costax.users.boundary;

import io.costax.users.entity.User;
import org.hibernate.jpa.QueryHints;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class Users {

    @PersistenceContext
    EntityManager em;

    // NOTES: We can not declare the TransactionAttributeType.SUPPORTS, it must be leaved as default or without any annotation (REQUIRED)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<User> list() {
        final List<User> users = em.createQuery("select u from User u left join fetch u.permissions order by u.id desc", User.class)
                .setHint(QueryHints.HINT_CACHEABLE, true)
                // NOTES:
                // For the purpose of the current example, it is not mandatory or required to use regions.
                // I'm using it just for the sake of coaching.
                // The region name can be any name we want, ex. Home-page, Top10, etc.
                .setHint(QueryHints.HINT_CACHE_REGION, "home")
                .getResultList();

        // NOTES:
        // This is the necessary workaround to ensure that all permissions entities are not proxies instances :(
        // We need to use one of the fowling options, I prefer the first one because it not requires a hibernate import
        users.forEach(user -> user.getPermissions().size());
        //users.forEach(user -> Hibernate.initialize(user.getPermissions()));

        return users;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public User add(User user) {
        final User merge = em.merge(user);

        em.flush();

        return merge;
    }

    public User getById(final Long id) {
        final EntityGraph<User> entityGraph = em.createEntityGraph(User.class);
        entityGraph.addAttributeNodes("permissions");

        final User user = em
                .find(User.class,
                        id,
                        Map.of(
                                org.hibernate.annotations.QueryHints.LOADGRAPH, entityGraph,
                                org.hibernate.annotations.QueryHints.CACHEABLE, true));

        // NOTES:
        // This is the necessary workaround to ensure that all permissions entities are not proxies instances :(
        // We need to use one of the fowling options, I prefer the first one because it not requires a hibernate import
        //users.forEach(user -> user.getPermissions().size());
        //users.forEach(user -> Hibernate.initialize(user.getPermissions()));
        Optional.ofNullable(user).ifPresent(u -> u.getPermissions().size());

        return user;
    }
}
