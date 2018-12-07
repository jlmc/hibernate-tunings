package io.costax.hibernatetuning.howtos.onetoonefkwrongside;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "game", schema = "workarounds")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, updatable = false, nullable = false)
    private Long id;

    private String home;
    private String visitor;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "game_report_id")
    private GameReport report;

    protected Game() {
    }

    public Game(final String home, final String visitor) {
        this.home = home;
        this.visitor = visitor;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Game game = (Game) o;
        return getId()!= null && Objects.equals(getId(), game.getId());
    }

    @Override
    public int hashCode() {
        return 31;
    }


    public void setReport(final GameReport report) {
        this.report = report;
    }

    public Long getId() {
        return id;
    }

    public String getHome() {
        return home;
    }

    public String getVisitor() {
        return visitor;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", home='" + home + '\'' +
                ", visitor='" + visitor + '\'' +
                '}';
    }
}
