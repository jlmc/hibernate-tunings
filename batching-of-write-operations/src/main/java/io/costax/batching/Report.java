package io.costax.batching;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "report", schema = "multimedia")
public class Report {

    @Id
    private Integer id;
    private String name;
    private Status status;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Report)) return false;
        final Report relatorio = (Report) o;
        return Objects.equals(id, relatorio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public enum Status {
        TODO(11), IN_PROGRESS(12), DONE(13);

        private static Map<Integer, Status> CACHE = Arrays.stream(values())
                .collect(Collectors.toMap(Status::getCode, Function.identity()));

        private final int code;

        Status(final int i) {
            this.code = i;
        }

        public static Status statusOf(Integer i) {
            final Status status = CACHE.get(i);
            if (status == null) throw new IllegalStateException("invalid status with the code '" + i + "'");
            return status;
        }

        public int getCode() {
            return code;
        }
    }

}
