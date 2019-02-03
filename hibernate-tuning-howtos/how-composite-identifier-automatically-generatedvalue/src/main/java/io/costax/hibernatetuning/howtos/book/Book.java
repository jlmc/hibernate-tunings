package io.costax.hibernatetuning.howtos.book;

import javax.persistence.*;

@Entity
@Table(name = "book")
@IdClass(PK.class)
public class Book {

    @Id
    @Column(name = "registration_number")
    @GeneratedValue(generator = "book_registration_number_seq", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "book_registration_number_seq", allocationSize = 1)
    private Long registrationNumber;

    @Id
    @Column(name = "publisher_id")
    private Integer publisherId;

    private String title;

    @Version
    private int version;

    protected Book() {}

    private Book(final int publisherId, final String title) {
        this.publisherId = publisherId;
        this.title = title;
    }

    public static Book of(final int publisherId, final String title) {
        return new Book(publisherId, title);
    }

    public int getPublisherId() {
        return publisherId;
    }

    public String getTitle() {
        return title;
    }

    public Long getRegistrationNumber() {
        return registrationNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}