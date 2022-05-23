package io.costax.hibernatetuning.howtos.ibook;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.SQLInsert;

@Entity
@Table(name = "book")
@SQLInsert( sql = "insert into book (title, publisher_id, version) values (?, ?, ?)")
public class IBook {

    @EmbeddedId
    private IBookKey key;

    private String title;

    @Version
    @Column(insertable = false)
    private Integer version;

    public IBook() {}

    public IBookKey getKey() {
        return key;
    }

    public void setKey(IBookKey key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "IBook{" +
                "key=" + key +
                ", title='" + title + '\'' +
                ", version=" + version +
                '}';
    }
}
