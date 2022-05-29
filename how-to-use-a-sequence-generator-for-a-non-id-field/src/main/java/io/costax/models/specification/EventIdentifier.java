package io.costax.models.specification;

import jakarta.persistence.*;

@Entity
@Table(name = "event_identifier")
public class EventIdentifier {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventIdentifierGen")
    @SequenceGenerator(name = "eventIdentifierGen", sequenceName = "event_identifier_seq", allocationSize = 6)
    private Integer id;

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return "EventIdentifier{" +
                "id=" + id +
                '}';
    }
}
