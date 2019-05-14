package io.costax.model;

import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import java.util.*;

public class IssueTreeResultTransformer implements ResultTransformer {

    private final EntityManager em;

    private Map<Long, Issue> issuesMap = new HashMap<>();

    private List<Issue> roots = new ArrayList<>();

    public IssueTreeResultTransformer(final EntityManager em) {
        this.em = em;
    }

    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        Issue issue = (Issue) tuple[0];
        Long parentId  = getParentId(tuple[1]);

        em.detach(issue);
        issue.setSubIssues(new LinkedList<>());

        if (parentId == null) {
            roots.add(issue);
        } else {
            final Issue parent = issuesMap.get(parentId);
            if(parent != null) {
                parent.addChild(issue);
            }
        }

        issuesMap.putIfAbsent(issue.getId(), issue);

        return issue;
    }

    @Override
    public List transformList(final List collection) {
        return roots;
    }

    private Long getParentId(Object o) {
        if (o instanceof Number) {
            Long id = ((Number) o).longValue();
            if (id > 0) {
                return id;
            }
        }

        return null;
    }
}
