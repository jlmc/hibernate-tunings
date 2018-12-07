package io.costax.hibernatetuning.howtos.onetoonefkwrongside;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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
    @AttributeOverride(name="home",  column= @Column(name="score_home", columnDefinition = ""))
    @AttributeOverride(name="visitor",  column= @Column(name="score_visitor"))
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
