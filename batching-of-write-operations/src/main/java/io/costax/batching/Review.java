package io.costax.batching;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "review", schema = "multimedia")
public class Review {

    @Id
    private Integer id;
    private String comment;
    private Rating rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false, updatable = false)
    private Book book;


    public Integer getId() {
        return id;
    }

    public String getComment() {
        return comment;
    }

    public Rating getRating() {
        return rating;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        final Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public enum Rating {
        //TODO(11), IN_PROGRESS(12), DONE(13);

        ONE(11), TWO(12), THREE(13), FOUR(14), FIVE(15);

        private static Map<Integer, Rating> CACHE = Arrays.stream(values())
                .collect(Collectors.toMap(Rating::getCode, Function.identity()));

        private final int code;

        Rating(final int i) {
            this.code = i;
        }

        public static Rating statusOf(Integer i) {
            final Rating status = CACHE.get(i);
            if (status == null) throw new IllegalStateException("invalid status with the code '" + i + "'");
            return status;
        }

        public int getCode() {
            return code;
        }

    }

}
