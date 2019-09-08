package io.costax.relationships.onetomany;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class TvProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tv_program_seq")
    //@SequenceGenerator(name="scene_id_generator", sequenceName = "tv_program_seq", allocationSize = 5, initialValue = 1)
    private Integer id;

    private LocalTime start;

    private LocalTime end;

    private String content;

    protected TvProgram() {}

    private TvProgram(final LocalTime start, final LocalTime end, final String content) {
        this.start = start;
        this.end = end;
        this.content = content;
    }

    public static TvProgram of(final LocalTime start, final LocalTime end, final String content) {
        return new TvProgram(start, end, content);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TvProgram)) return false;
        final TvProgram tvProgram = (TvProgram) o;
        return this.id != null && Objects.equals(id, tvProgram.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }


    public LocalTime getStart() {
        return start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public String getContent() {
        return content;
    }
}
