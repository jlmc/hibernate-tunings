package io.costax.trading.finex.control;

import io.costax.core.persistence.Specification;
import io.costax.trading.finex.entity.Account;
import io.costax.trading.finex.entity.SearchFilter;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public final class AccountSpecifications {

    public static Specification<Account> byOwner(final String owner) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            Path<String> ownerPath = root.get("owner");
            Expression<String> literal = criteriaBuilder.literal(owner);
            predicates.add(criteriaBuilder.equal(criteriaBuilder.trim(criteriaBuilder.lower(ownerPath)), criteriaBuilder.trim(criteriaBuilder.lower(literal))));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Account> by(final SearchFilter filter) {
        return (root, query, criteriaBuilder) -> {
            if (filter != null) {
                List<Predicate> predicates = new ArrayList<>();
                if (filter.getOwner() != null) {
                    Predicate predicate = byOwner(filter.getOwner()).toPredicate(root, query, criteriaBuilder);
                    predicates.add(predicate);
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            } else {
                return criteriaBuilder.conjunction();
            }
        };

    }
}
