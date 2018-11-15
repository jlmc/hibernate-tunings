# Inheritance SINGLE_TABLE

The inherintance default strategy is the single table. In this file you can find a a default configuration using single table:

## advantage
- The great advantage of the strategy is the performance, since the information is all in the same table, it is not necessary Joins.
- very useful when the number of subclasses is relatively small, otherwise the number of rows will grow to large.


## disadvantage
From a data integrity perspective, the single table Inheritance limitation defeats the purpose of consistency in the context of ACID properties.
To eliminate this disadvantage, we can resort to the following well-known strategies: 

- Bean Validation 
- Application level checks

We can enforce NOT NULL constraints at the database table level with:

- CHECK (PostgresSQL)
- TRIGGER (MySQL)



##
```java
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
    List<FinancialDocument> financialDocuments = new ArrayList<>();
    
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
```


# Basic base entity class:

```java
package io.costa.hibernatetunings.entities.financial;
    
    
import org.hibernate.annotations.CreationTimestamp;
    
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;
    
@Entity
@Table(name = "financial_document")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class FinancialDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Version
    private short version;
    
    @CreationTimestamp
    @Column(name = "created_on")
    private OffsetDateTime createAt;
    
    @Column(name = "value")
    private BigDecimal value = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false, updatable = false)
    private Board board;
    
    protected FinancialDocument() {}
    
    protected FinancialDocument(final BigDecimal value) {
        this.value = value;
    }
    
    protected void setBoard(final Board board) {
        this.board = board;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FinancialDocument that = (FinancialDocument) o;
        return this.id != null && Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return 31;
    }
}
```

## basic extends 

```java
@Entity
public class Invoice extends FinancialDocument {
    
    @Column(name = "content")
    private String content;
    
    protected Invoice() {
    }
    
    private Invoice(final BigDecimal value, final String content) {
        super(value);
        this.content = content;
    }
    
    public static Invoice of(final BigDecimal value, final String content) {
        return new Invoice(value, content);
    }
}
```




```java
@Entity
public class CreditNote extends FinancialDocument {
    
    @Column(name = "expiration_on")
    private OffsetDateTime expirationOn;
    
    protected CreditNote() {
    }
    
    private CreditNote(final BigDecimal value, final OffsetDateTime expirationOn) {
        super(value);
        this.expirationOn = expirationOn;
    }
    
    public static CreditNote of(final BigDecimal value, final OffsetDateTime expirationOn) {
        return new CreditNote(value, expirationOn);
    }
}
```

### Example of trigger MySQL

```mysql
CREATE TRIGGER post_content_check BEFORE INSERT ON financial_document
    FOR EACH ROW 
    BEGIN 
       IF NEW.DTYPE = 'Invoice' 
       THEN 
           IF NEW.content IS NULL
           THEN
               signal sqlstate '45000'
               set message_text = 'Invoice content cannot be NULL'; 
           END IF; 
       END IF; 
    END;
```