package io.costax.relationships.onetomany;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class TvChannel {

    @Id
    private String id;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderColumn(name = "entry")
    private Set<TvProgram> programs = new HashSet<>();

    protected TvChannel() {
    }

    private TvChannel(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static TvChannel of(final String id, final String name) {
        return new TvChannel(id, name);
    }

    public void addProgram(TvProgram tvProgram) {
        this.programs.add(tvProgram);
    }

    public void removeProgram(TvProgram tvProgram) {
        this.programs.remove(tvProgram);
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<TvProgram> getPrograms() {
        return Set.copyOf(programs);
    }
}
