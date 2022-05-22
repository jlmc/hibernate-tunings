package io.costax.hibernatetuning.howtos.onetoonefkwrongside;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_report", schema = "workarounds")
public class GameReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private Long id;

    @CreationTimestamp
    private LocalDateTime resisted;

    @Embedded
    @AttributeOverride(name = "home", column = @Column(name = "score_home", columnDefinition = ""))
    @AttributeOverride(name = "visitor", column = @Column(name = "score_visitor"))
    private Score score = new Score();


    @Basic(fetch = FetchType.LAZY)
    private byte[] events;

    public GameReport() {
    }

    public GameReport(final Score score, final byte[] events) {
        this.score = score;
        this.events = events;
    }
}
