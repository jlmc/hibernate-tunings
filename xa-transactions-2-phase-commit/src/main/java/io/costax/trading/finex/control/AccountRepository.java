package io.costax.trading.finex.control;

import io.costax.core.persistence.Specification;
import io.costax.trading.finex.entity.Account;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AccountRepository {

    @PersistenceContext(unitName = "dev1")
    EntityManager em;

    public List<Account> listAll() {
        return em.createQuery("select a from Account a order by trim(a.owner)", Account.class)
                .getResultList();
    }

    public List<Account> listAll(Specification<Account> specification) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
        Root<Account> from = criteriaQuery.from(Account.class);

        return em.createQuery(
                criteriaQuery
                        .where(specification.toPredicate(from, criteriaQuery, criteriaBuilder))
                .orderBy(criteriaBuilder.asc(criteriaBuilder.trim(criteriaBuilder.lower(from.get("owner")))))
            ).getResultList();
    }

    public Account save(Account account) {
        Objects.requireNonNull(account);

        if (account.getId() != null) {
            return em.merge(account);
        } else {
            em.persist(account);
            return account;
        }
    }

    public void flush() {
        em.flush();
    }

    public Optional<Account> findById(final UUID id) {
        Account account = em.find(Account.class, id);
        return Optional.ofNullable(account);
    }
}
