package io.costax.model;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;

import java.util.Objects;

@Entity
@NamedNativeQuery(
        name = "get_rec_nr_serie_document",
        resultSetMapping = "SerieDocNumMapping",
        query = "select id as serieDocumentoId, name as title, indicator as numDoc " +
                "from get_rec_nr_serie_document( :id ) as datos(id int, indicator int, name varchar)"
)
@SqlResultSetMapping(name = "SerieDocNumMapping",
        classes = @ConstructorResult(
                targetClass = SerieDocNum.class,
                columns = {
                        @ColumnResult(name = "serieDocumentoId"),
                        @ColumnResult(name = "title"),
                        @ColumnResult(name = "numDoc")
                }
        )
)
public class SerieDocument {

    @Id
    private Integer id;
    private int indicator = 0;
    private String name;

    public Integer getId() {
        return id;
    }

    public int getIndicator() {
        return indicator;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SerieDocument)) return false;
        final SerieDocument that = (SerieDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SerieDocument{" +
                "id=" + id +
                ", indicator=" + indicator +
                ", name='" + name + '\'' +
                '}';
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setIndicator(final int indicator) {
        this.indicator = indicator;
    }
}
