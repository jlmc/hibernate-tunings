package io.costax.jpa.fetching;

import io.costax.jpa.util.HSQLDBJPATest;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static java.time.LocalDate.parse;

/**
 * By using Hibernate’s 1st level cache and its guarantee that within a Hibernate Session,
 * a database record gets only mapped by 1 entity object, you can implement this very efficiently.
 * Your 1st query gets all the Book entities and their Publisher, which you need for your use case.
 */
public class BestWayToFetchAnAssociationDefinedByASubclass extends HSQLDBJPATest {

    @Override
    protected Class<?>[] entities() {
        return new Class[]{
                Publication.class,
                Book.class,
                BlogPost.class,
                Publisher.class,
                Author.class
        };
    }

    @Override
    protected void additionalProperties(final Properties properties) {
        properties.put("hibernate.format_sql", "true");
    }

    @Override
    protected void afterInit() {
        // create some records
        doInJPA(em -> {

            Author luisCamoes = new Author("Luis", "Camões");
            Author fernandoPessoa = new Author("Fernando", "Pessoa");
            Author joaoCosta = new Author("Joao", "Costa");

            em.persist(luisCamoes);
            em.persist(fernandoPessoa);
            em.persist(joaoCosta);

            final Publisher publisherPT = new Publisher("Portugal a ler");
            em.persist(publisherPT);

            fernandoPessoa.add(
                    new Book("O banqueiro anarquista", parse("1922-01-01"), 123, publisherPT));
            fernandoPessoa.add(
                    new Book("Mensagem", parse("1934-01-01"), 123, publisherPT));
            luisCamoes.add(
                    new Book("Lusiadas", parse("1572-01-01"), 123, publisherPT));
            joaoCosta.add(
                    new BlogPost("Best Way To Fetch An Association Defined By A Subclass", parse("2020-06-22"), "xxx"));
        });

    }

    @Test
    void test() {
        final List<Publication> publications1 =
                doInJPA(em -> {

                    final List<Book> booksWithPublisherFetched = em.createQuery("""
                                        select b 
                                        from Book b 
                                        join b.author a 
                                        join fetch b.publisher p 
                                        where a.lastName = :lastName
                                    """
                            , Book.class)
                            .setParameter("lastName", "Pessoa")
                            .getResultList();

                    for (Book b : booksWithPublisherFetched) {
                        LOGGER.info("{}", b);
                    }

                    final List<Publication> publications = em.createQuery("""
                            select p 
                            from Publication p 
                            join p.author a 
                            where a.lastName = :lastName
                            """, Publication.class)
                            .setParameter("lastName", "Pessoa")
                            .getResultList();

                    return publications;
                });


        for (Publication p : publications1) {
            if (p instanceof BlogPost) {
                BlogPost blog = (BlogPost) p;
                LOGGER.info("BlogPost - " + blog.title + " was published at " + blog.url);
            } else {
                Book book = (Book) p;
                LOGGER.info("Book - " + book.title + " was published by " + book.publisher.name);
                LOGGER.info("{}", book);
            }
        }
    }

    @Entity(name = "Publication")
    @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
    //@DiscriminatorColumn()
    static abstract class Publication {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        protected Long id;

        protected String title;

        @Version
        protected int version;

        @ManyToOne(fetch = FetchType.LAZY)
        protected Author author;

        protected LocalDate publishingDate;

        protected Publication() {
        }

        public Publication(final String title, final LocalDate publishingDate) {
            this.title = title;
            this.publishingDate = publishingDate;
        }

        public void setAuthor(final Author author) {
            this.author = author;
        }
    }

    @Entity(name = "BlogPost")
    @DiscriminatorValue("BlogPost")
    static class BlogPost extends Publication {

        private String url;

        BlogPost() {
        }

        public BlogPost(final String title, final LocalDate publishingDate, final String url) {
            super(title, publishingDate);
            this.url = url;
        }
    }

    @Entity(name = "Book")
    @DiscriminatorValue("Book")
    static class Book extends Publication {

        private int pages;

        @ManyToOne
        private Publisher publisher;

        Book() {
        }

        public Book(final String title, final LocalDate publishingDate, final int pages, final Publisher publisher) {
            super(title, publishingDate);
            this.pages = pages;
            this.publisher = publisher;
        }
    }

    @Entity(name = "Publisher")
    static class Publisher {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;
        private String name;

        Publisher() {
        }

        public Publisher(final String name) {
            this.name = name;
        }
    }

    @Entity(name = "Author")
    static class Author {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String firstName;
        private String lastName;

        @Version
        private int version;

        @OneToMany(mappedBy = "author", cascade = {CascadeType.ALL}, orphanRemoval = true)
        private final Set<Publication> publications = new HashSet<>();

        Author() {
        }

        public Author(final String firstName, final String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public void add(Publication publication) {
            publication.setAuthor(this);
            publications.add(publication);
        }

    }
}
