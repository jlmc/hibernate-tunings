package io.costax.model;

import org.hibernate.transform.ResultTransformer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IssueNodeTreeResultTransformer implements ResultTransformer {

    private Map<Long, IssueNodeTree> issuesMap = new HashMap<>();
    private List<IssueNodeTree> roots = new ArrayList<>();

    @Override
    public Object transformTuple(final Object[] tuple, final String[] aliases) {
        IssueNodeTree issueNodeTree = (IssueNodeTree) tuple[0];
        Long parentId = issueNodeTree.getParentId();

        if (parentId == null) {
            roots.add(issueNodeTree);
        } else {
            IssueNodeTree parent = issuesMap.get(parentId);
            if (parent != null) {
                parent.addChild(issueNodeTree);
            }
        }
        issuesMap.putIfAbsent(issueNodeTree.getId(), issueNodeTree);

        return issueNodeTree;
    }

    @Override
    public List transformList(final List collection) {
        return roots;
    }
}
