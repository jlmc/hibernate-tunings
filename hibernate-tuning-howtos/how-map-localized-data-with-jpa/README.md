# Localized Data – How to Map It With Hibernate


- Internationalization not only affects the UI. If your application stores user-generated data and supports multiple languages, you also need a way to store different translations in your database. Typical examples are:

- market places that allow you to provide Beer descriptions in various languages,
travel sites that offer trips to people all over the world and
document management systems that store document descriptions and keywords for multiple languages.

- In all of these examples, you need to localize your frontend and parts of the persisted data. The two most common approaches for that are:

## Using Java ResourceBundle

This standard Java feature provides a simple to use and very efficient option to implement internationalization. You need to provide a properties file for each locale you want to support. You can then use the ResourceBundle class to get the property for the currently active Locale.
The only downside of this approach is that the different translations are hard to maintain. If you want to add, change, or remove the translation of a property, you need to edit one or more properties files. In the worst case, that might even require a re-deployment of your application.
That makes Java’s ResourceBundle a good option for all static, pre-defined texts, like general messages or attribute names that you use in your UI. But if you want to translate user-generated content or any other String that gets often changed, you should prefer a different approach.

## Storing translations in the database

You get more flexibility, and updating a translated name or description is much easier if you persist the localized data in your database. Adding or changing a translation, then only requires the execution of an SQL INSERT or UPDATE statement. That makes it a great approach for all user-generated content.
Unfortunately, the implementation is also more complicated. There is no standard Java feature that you can easily use. You need to design your table model accordingly, and you need to implement the read and update routines yourself.


### Different Ways to Store Localized Data

1. Using separate columns for each language in the same database table, e.g., modeling the columns description_en and description_de to store different translations of a beer description.

2. Storing translated fields in a separate table. That would move the description_en and description_de columns to a different table. Let’s call it LocalizedBeer.


#### 1. Separate Language Columns in Each Table

- The general idea of this approach is simple. 
- For each localized attribute and language you need to support, you add an extra column to your table. 
- Depending on the number of supported languages and localized attributes, this can result in a vast amount of additional columns. 
- If you want to translate 4 attributes into 5 different languages, you would need to model 4*5=20 database columns.

```
@Entity
public class Beer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
     
    private Double price;
    
    @Column(name = "name_de")
    private String nameDe;
     
    @Column(name = "name_en")
    private String nameEn;
 
    @Column(name = "description_de")
    private String descriptionDe;
     
    @Column(name = "description_en")
    private String descriptionEn;
}
```

##### Pros & Cons of Entities with Separate Language Columns

- Pros 
    - is very easy to implement in the table model.
    - is very easy to map to an entity.
    - enables you to fetch all translations with a simple query that doesn’t require any JOIN clauses.
  
- Cons
    - this mapping might require a lot of database columns if you need to translate multiple attributes into various languages.
    - fetching an entity loads translations that you might not use in your use case.
    - you need to update the database schema if you need to support a new language.
    - the inflexibility of this approach is the biggest downside.
        - If your application is successful, your users and sales team will request additional translations.
    

#### 2. Different Tables and Entities for Translated and Non-Translated Fields

We can separate the translated and non-translated fields into 2 tables. That enables you to model a one-to-many association between the non-translated fields and the different localizations.

```java
@Entity
@Table(name = "t_beer")
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Beer {

    @Id
    private Long id;

    @NotNull
    @DecimalMin("0.1")
    private BigDecimal price;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Version
    private int version;

    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
    @OneToMany(mappedBy = "beer",
            orphanRemoval = true, // because we are using orphanRemoval we could omit CascadeType.REMOVE
            cascade = {CascadeType.DETACH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST,
                    CascadeType.REFRESH,
                    CascadeType.REMOVE})
    //@MapKey(name = "localizedId.locale")
    @MapKey(name = "localizedId.name")
    private Map<String, LocalizedBeer> localizations = new HashMap<>();
}


```

``` java
@Embeddable
public class LocalizedId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String locale;
    private String name;
}
```

```
//@Cacheable
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Entity
@Table(name = "t_localized_beer")
public class LocalizedBeer {

    @EmbeddedId
    private LocalizedId localizedId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id", nullable = false)
    private Beer beer;

    private String description;

    public LocalizedBeer() {
    }

    public LocalizedBeer(final Beer beer, final String locale, final String name, final String description) {
        this.beer = beer;
        this.description = description;
        this.localizedId = new LocalizedId(beer.getId(), locale, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalizedBeer that = (LocalizedBeer) o;
        return Objects.equals(beer, that.beer) &&
                Objects.equals(localizedId, that.localizedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beer, localizedId);
    }

    @Override
    public String toString() {
        return "LocalizedBeer{" +
                "localizedId=" + localizedId +
                ", description='" + description + '\'' +
                '}';
    }
}
```


- You can do that by activating the cache in your persistence.xml configuration and by annotating the LocalizedBeer entity with JPA’s @Cacheable or Hibernate’s @Cache annotation.
- caching is a two-edged sword. It can provide substantial performance benefits but also introduce an overhead which can slow down your application.


##### Pros & Cons of Entities with Separate Language Columns

- Pros 
    - Each new translation is stored as a new record in the LocalizedBeer table. That enables you to store new translations without changing your table model.
    - Hibernate’s 2nd level cache provides an easy way to cache the different localizations. In my experience, other attributes of an entity, e.g., the price, change more often than the translations of a name or description. It can, therefore, be a good idea to separate the localizations from the rest of the data to be able to cache them efficiently.

- Cons
    - If you want to access the localized attributes, Hibernate needs to execute an additional query to fetch the associated LocalizedBeer entities. You can avoid that by initializing the association when loading the Beer entity.
    - Fetching associated LocalizedBeer entities might load translations that you don’t need for your use case.

    - this mapping might require a lot of database columns if you need to translate multiple attributes into various languages.
    - fetching an entity loads translations that you might not use in your use case.
    - you need to update the database schema if you need to support a new language.
    - the inflexibility of this approach is the biggest downside.
        - If your application is successful, your users and sales team will request additional translations.
    