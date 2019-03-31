package batching;

import org.hibernate.type.TimestampType;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "author", schema = "multimedia")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actorGenerator")
    @SequenceGenerator(
            name = "actorGenerator",
            sequenceName = "author_10_seq",
            schema = "multimedia",
            initialValue = 100,
            allocationSize = 10)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Version
    @Column(name = "version")
    private int version;

    /**
     * DbTimestampType
     *
     * <tt>dbtimestamp</tt>: An extension of {@link TimestampType} which
     * maps to the database's current timestamp, rather than the jvm's
     * current timestamp.
     * <p/>
     * Note: May/may-not cause issues on dialects which do not properly support
     * a true notion of timestamp (Oracle < 8, for example, where only its DATE
     * datatype is supported).  Depends on the frequency of DML operations...
     */
    //@Version
    //@Type(type = "dbtimestamp")
    //@Generated(GenerationTime.ALWAYS)
    @Column(name = "mod_date")
    private Date modDate;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @ManyToMany(mappedBy = "authors", cascade = CascadeType.ALL)
    private Set<Book> books = new HashSet<>();

    protected Author() {
    }

    private Author(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static Author of(final String firstName, final String lastName) {
        return new Author(firstName, lastName);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        final Author actor = (Author) o;
        return this.id != null && Objects.equals(id, actor.id);
    }

    @Override
    public int hashCode() {
        return 31;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public Set<Book> getBooks() {
        return Collections.unmodifiableSet(books);
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Date getModDate() {
        return modDate;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }
}
