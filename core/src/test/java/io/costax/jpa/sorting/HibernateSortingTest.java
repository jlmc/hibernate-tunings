package io.costax.jpa.sorting;

import io.costax.jpa.util.HSQLDBJPATest;
import org.hibernate.annotations.QueryHints;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <h1>Hibernate @Sorting Annotations</h1>
 *
 * <p>
 * You can tell Hibernate to use natural sorting by annotating the association attribute with @SortNatural.
 * This approach uses the Comparable implementation of the related entities.
 * </p>
 *
 * <p>
 *     To see the possibilities and effects of then we should edit the property {@link Seller#products}
 * </p>
 */
public class HibernateSortingTest extends HSQLDBJPATest {

    @Override
    protected void additionalProperties(final Properties properties) {
        properties.put("hibernate.format_sql", "true");
    }

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                Product.class,
                Seller.class
        };
    }

    @BeforeEach
    void setUp() {
        doInJPA(em -> {


            final List<Product> products =
                    Stream.iterate(1L, i -> i < 11, i -> i + 1)
                            .map(i -> Product.of(i, i + ".Beer"))
                            .collect(Collectors.toList());

            final List<Seller> sellers =
                    Stream.iterate(1L, i -> i < 11, i -> i + 1)
                            .map(i -> {
                                final Seller seller = Seller.of(i, i + "-Beer Shop");

                                for (int y = 0; y < i; y++)
                                    seller.addProduct(products.get(y));

                                return seller;
                            })
                            .collect(Collectors.toList());

            sellers.forEach(em::persist);


        });
    }

    @AfterEach
    void tearDown() {
        doInJPA(em -> {
            em.createNativeQuery("delete from seller_product where true").executeUpdate();
            em.createNativeQuery("delete from Product where true").executeUpdate();
            em.createNativeQuery("delete from Seller where true").executeUpdate();
        });
    }

    @Test
    void findSellersWithItProducts() {

        final List<Seller> sellers = doInJPA(em -> {

            return em.createQuery("select distinct s from Seller s left join fetch s.products", Seller.class)
                    .setHint(QueryHints.PASS_DISTINCT_THROUGH, false)
                    .getResultList();

        });

        sellers.forEach(seller -> {

            System.out.println("-- " + seller + ": " + seller.getProductTitles());

        });

    }

    // ********************************

    @Entity(name = "Seller")
    public static class Seller implements Comparable<Seller> {

        @Id
        //@GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", updatable = false, nullable = false)
        private Long id;

        @Version
        private int version;

        private String name;

        @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
        @JoinTable(name = "seller_product",
                joinColumns = @JoinColumn(name = "seller_id", nullable = false, updatable = false),
                inverseJoinColumns = @JoinColumn(name = "product_id", nullable = false, updatable = false)
        )
        // 1. possibility, using the JPA specification, in terms of performance this is the best option
        //@OrderBy("title desc ")
        //private Set<Product> products = new HashSet<>();

        // 2. possibility, Hibernate Natural sorting
        //@org.hibernate.annotations.SortNatural
        //private SortedSet<Product> products = new TreeSet<Product>();

        // 3. possibility, Hibernate Custom Comparator sorting
        @org.hibernate.annotations.SortComparator(ProductTitleDescComparator.class)
        private SortedSet<Product> products = new TreeSet<Product>();


        //@formatter:off
        public Seller() { }
        //@formatter:on

        private Seller(Long id, final String name) {
            this.id = id;
            this.name = name;
        }

        public static Seller of(Long id, final String name) {
            return new Seller(id, name);
        }

        @Override
        public int compareTo(Seller o) {
            System.out.printf("--> comparing %s with %s", this, o);
            return name.compareTo(o.name);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Seller seller = (Seller) o;
            return Objects.equals(id, seller.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Seller{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        public void addProduct(Product b) {
            products.add(b);
            b.addSeller(this);
        }

        public void removeProduct(Product b) {
            products.remove(b);
            b.removeSeller(this);
        }

        @Transient
        public String getProductTitles() {
            return this.products.stream()
                    .map(Product::getTitle)
                    .collect(Collectors.joining("; ", "[ ", " ]"));
        }
    }

    @Entity(name = "Product")
    private static class Product implements Comparable<Product> {

        @Id
        //@GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id", updatable = false, nullable = false)
        private Long id;

        @Version
        private int version;

        private String title;

        @ManyToMany(mappedBy = "products")
        private Set<Seller> sellers = new HashSet<>();

        //@formatter:off
        Product() { }
        //@formatter:on


        private Product(final Long id, final String title) {
            this.id = id;
            this.title = title;
        }

        public static Product of(final Long id, final String title) {
            return new Product(id, title);
        }

        @Override
        public int compareTo(Product o) {
            System.out.printf("comparing %s with %s\n", this, o);
            return title.compareTo(o.title);
        }

        public void addSeller(final Seller seller) {
            this.sellers.add(seller);
        }

        public void removeSeller(final Seller seller) {
            this.sellers.remove(seller);
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final Product product = (Product) o;
            return Objects.equals(id, product.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", title='" + title + '\'' +
                    '}';
        }

        public Long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }

    public static class ProductTitleDescComparator implements Comparator<Product> {

        public ProductTitleDescComparator() {
        }

        @Override
        public int compare(final Product o1, final Product o2) {
            System.out.println("==== Custom Comparator " + o1 + " -- " + o2);
            // in inverse order
            return o2.getTitle().compareTo(o1.getTitle());
        }
    }


}
