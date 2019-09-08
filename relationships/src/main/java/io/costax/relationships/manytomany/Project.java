package io.costax.relationships.manytomany;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "project")
public class Project {

    @Id
    private Integer id;
    private String name;

    /**
     * For `@ManyToMany` associations, {@link CascadeType.REMOVE} does not make too much sense when both sides represent independent entities.
     * In this case, removing a {@code Project } entity should not trigger a Script removal because the {@code Developer} can be referenced by other {@code Project } as well.
     * The same arguments apply to orphan removal since removing an entry from the tags collection should only delete the junction record and not the target {@code Developer} entity.
     * For both unidirectional and bidirectional associations, it is better to avoid the {@link CascadeType.REMOVE} mapping.
     * Instead of {@code @ManyToMany(cascade = CascadeType.ALL )}, the cascade attributes should be declared explicitly
     * (e.g. {@code @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE } )})
     *
     *
     * Use a List it is not a good idea,
     * because if it is necessary to remove one single reference from the collection:
     * 1.DELETE statement will be trigger : {@code delete from project_developer where project_id = :id}
     * 2.Many INSERT statements will be trigger with records that already have been inserted before.
     * A Set collection have a much better performance but as consequence the @OrderColumn(name = "li")
     * don't work.
     *
     */
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "project_developer",
            joinColumns = @JoinColumn(
                    name = "project_id",
                    nullable = false,
                    updatable = false),
            inverseJoinColumns = @JoinColumn(
                    name = "developer_id",
                    nullable = false,
                    updatable = false)
    )
    // @OrderColumn only works when the collections is a List, and the li is a unmapped column
    //@OrderColumn(name = "li")

    // Add a order by to the fetch query to the order by parent.field
    @OrderBy("id desc ")
    private Set<Developer> developers = new HashSet<>();


    protected Project() {
    }

    private Project(final Integer id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Project of(final Integer id, String name) {
        return new Project(id, name);
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        final Project project = (Project) o;
        return Objects.equals(getId(), project.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
    public void addDeveloper(final Developer developer) {
        developers.add(developer);
    }

    public void removeDeveloper(final Developer developer) {
        developers.remove(developer);
    }

    public Set<Developer> getDevelopers() {
        return Set.copyOf(developers);
    }

    public int getTeamSize() {
        return developers.size();
    }
}
