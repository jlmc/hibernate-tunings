package io.costa.hibernatetunings.entities.financial;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private short version;

    @NaturalId
    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "board",
            orphanRemoval = true,
            cascade = CascadeType.ALL)
    private List<FinancialDocument> financialDocuments = new ArrayList<>();

    protected Board() {
    }

    private Board(final String code, final String name) {
        this.name = name;
        this.code = code;
    }

    public static Board of(String code, final String name) {
        return new Board(code, name);
    }

    public void add(FinancialDocument fd) {
        this.financialDocuments.add(fd);
        fd.setBoard(this);
    }

    @PreUpdate
    @PrePersist
    private void codeToLower() {
        if (this.code != null) {
            this.code = code.toLowerCase();
        }
    }

}
