package io.costax.trading.finex.control;

import io.costax.core.persistence.Specification;
import io.costax.trading.finex.entity.AuditLog;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class AuditLogRepository {

    @PersistenceContext(unitName = "dev2")
    EntityManager em;

    public List<AuditLog> listAll() {
        return em.createQuery("select a from AuditLog a order by a.at desc", AuditLog.class)
                .getResultList();
    }

    public AuditLog save(AuditLog account) {
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

    public Optional<AuditLog> findById(final UUID id) {
        AuditLog account = em.find(AuditLog.class, id);
        return Optional.ofNullable(account);
    }

    public List<AuditLog> findAllBy(final Specification<AuditLog> specification) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<AuditLog> criteriaQuery = builder.createQuery(AuditLog.class);
        Root<AuditLog> root = criteriaQuery.from(AuditLog.class);

        Predicate predicate = specification.toPredicate(root, criteriaQuery, builder);

        return em.createQuery(
                criteriaQuery
                        .where(predicate)
                        .orderBy(builder.desc(root.get("at"))))
                .getResultList();

    }
}
