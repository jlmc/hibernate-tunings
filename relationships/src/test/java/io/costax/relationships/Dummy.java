package io.costax.relationships;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Dummy {


    @Test
    public void bd() {
        final BigDecimal a = new BigDecimal(""+ 0.1D);

        final BigDecimal b = new BigDecimal("0.1");

        System.out.println(a);
        System.out.println(b);

        final BigDecimal c = new BigDecimal(Double.NaN);


    }

    @Test
    public void name() {

        final List<Foo> foos = List.of(
                new Foo(false, LocalDate.of(2019, 2, 2), "a"),
                new Foo(true, LocalDate.of(2018, 10, 1), "b"),
                new Foo(false, LocalDate.of(2019, 1, 1), "c"),
                new Foo(true, LocalDate.of(2018, 9, 11), "d"),
                new Foo(false, LocalDate.of(2019, 1, 4), "e"),
                new Foo(true, LocalDate.of(2018, 6, 8), "f")
        );






        final Comparator<Foo> reversed = Comparator.comparing(Foo::isActive)
                .thenComparing(Foo::getDate)
                .reversed();
        ;


        final List<Foo> collect = foos.stream().sorted(reversed).collect(Collectors.toList());

        collect.forEach(System.out::println);


    }

    private static class Foo {

        private boolean active;

        private LocalDate date;

        private String k;

        public Foo(final boolean active, final LocalDate date, final String k) {
            this.active = active;
            this.date = date;
            this.k = k;
        }

        public boolean isActive() {
            return active;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getK() {
            return k;
        }

        @Override
        public String toString() {
            return "Foo{" +
                    "active=" + active +
                    ", date=" + date +
                    ", k='" + k + '\'' +
                    '}';
        }
    }
}
