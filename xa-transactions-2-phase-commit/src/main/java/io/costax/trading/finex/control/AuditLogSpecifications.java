package io.costax.trading.finex.control;

import io.costax.core.persistence.Specification;
import io.costax.trading.finex.entity.AuditLog;
import io.costax.trading.finex.entity.SearchFilter;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSpecifications {

    public static Specification<AuditLog> byAuditLogFilter(final SearchFilter filter) {
        return (root, query, criteriaBuilder) -> {

            if (filter != null) {
                List<Predicate> predicates = new ArrayList<>();
                if (filter.getOwner() != null) {
                    Path<String> owner = root.get("owner");
                    Expression<String> literal = criteriaBuilder.literal(filter.getOwner());
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.trim(criteriaBuilder.lower(owner)), criteriaBuilder.trim(criteriaBuilder.lower(literal))));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            } else {
                return criteriaBuilder.conjunction();
            }
        };

    }

}
