# Hibernate 5 Naming Strategies

JPA and Hibernate provide a default mapping that maps each entity class to a database table with the same name.
Each of its attributes gets mapped to a column with the same, but what if you want to change this default, e.g., because it doesn’t match your company’s naming conventions?


You can, of course, specify the table name for each entity and the column name for each attribute.
That requires a `javax.persistence.Table` annotation on each class and a `javax.persistence.Column` annotation on each attribute. This is called an explicit naming.

That’s a good approach if you want to change the mapping for one attribute.
But doing that for lots of attributes requires a lot of work.
Adapting Hibernate’s naming strategy is then often a better approach.


## Implicit naming strategy

If you don’t set the table or column name in an annotation, Hibernate uses one of its implicit naming strategies.
You can choose between 4 different naming strategies and 1 default strategy:


- `default`:
    - By default, Hibernate uses the implicit naming strategy defined by the JPA specification.
    - This value is an alias for `jpa`.

- `jpa`:
    - This is the naming strategy defined by the JPA 2.0 specification.
    - The logical name of an entity class is either the name provided in the `javax.persistence.Entity` annotation or the unqualified class name.
    - For basic attributes, it uses the name of the attributes as the logical name.
    - To get the logical name of a join column of an association, this strategy concatenates the name of the referencing attribute, an “_” and the name of the primary key attribute of the referenced entity.
    - The logical name of a join column of an element collection consists of the name of the entity that owns the association, an “_” and the name of the primary key attribute of the referenced entity.
    - And the logical name of a join table starts with the physical name of the owning table, followed by an “_” and the physical name of the referencing table.

- `legacy-hbm`:
    - This is Hibernate’s original naming strategy.
    - It doesn’t recognize any of JPA’s annotations.
    - But you can use Hibernate’s proprietary configuration file and annotations to define a column or entity name.
    - In addition to that, there are a few other differences to the JPA specification:
        - The logical name of a join column is only its attribute name.
        - For join tables, this strategy concatenates the name of the physical table that owns the association, an “_” and the name of the attribute that owns the association.



- `legacy-jpa`:
    - The legacy-jpa strategy implements the naming strategy defined by JPA 1.0. The main differences to the jpa strategy are:
        - The logical name of a join table consists of the physical table name of the owning side of the association, an “_” and either the physical name of the referencing side of the association or the owning attribute of the association.
       - To get the logical name of the join column of an element collection, the legacy-jpa strategy uses the physical table name instead of the entity name of the referenced side of the association. That means the logical name of the join column consists of the physical table name of the referenced side of the association, an “_” and the name of the referenced primary key column.


- `component-path`:
    - This strategy is almost identical to the jpa strategy.
    -  The only difference is that it includes the name of the composite in the logical attribute name.



We can configure the logical naming strategy by setting the `hibernate.implicit_naming_strategy` attribute in the configuration:

```
<property name="hibernate.implicit_naming_strategy" value="jpa" />
```



## Physical naming strategy

Implementing our own physical naming strategy isn’t complicated.
We can either implement the `org.hibernate.boot.model.naming.PhysicalNamingStrategy` interface or extend Hibernate’s `PhysicalNamingStrategyStandardImpl` class.

In Java, we prefer to use camel case for our class and attribute names.
By default, Hibernate uses the logical name as the physical name.
So, for example, the entity attribute LocalDate publishingDate gets mapped to the database column publishingDate.

Some companies use naming conventions that require you to use snake case for your table and column names.
That means that your publishingDate attribute needs to be mapped to the publishing_date column.

We can activate the logical naming strategy by setting the `hibernate.physical_naming_strategy` attribute in the configuration:

```
<property name="hibernate.physical_naming_strategy" value="io.costax.persistence.api.SnakeCasePhysicalNamingStrategy" />
```


## References

- https://docs.jboss.org/hibernate/orm/5.1/userguide/html_single/chapters/domain/naming.html