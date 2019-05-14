package io.costax.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class IssueNodeTree {

    private Long id;
    private String title;
    private Long parentId;

    public IssueNodeTree(final Number id, final String title, final Number parentId) {
        this.id = id.longValue();
        this.title = title;
        this.parentId = parentId != null ? parentId.longValue() : null;
    }

    private List<IssueNodeTree> childrens = new ArrayList<>();
    
    public void addChild(IssueNodeTree child) {
        childrens.add(child);
    }

    public List<IssueNodeTree> getChildrens() {
        return List.copyOf(childrens);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IssueNodeTree)) return false;
        final IssueNodeTree that = (IssueNodeTree) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Long getParentId() {
        return parentId;
    }
}
