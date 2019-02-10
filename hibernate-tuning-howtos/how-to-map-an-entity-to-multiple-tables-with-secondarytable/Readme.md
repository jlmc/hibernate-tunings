# Primary and secondary table with JPA

Can JPA persist the attributes of some entity in several tables? 

The answer is yes. 

It has a performance impact (each simple query will use joins) but it can be very helpful when you need to map your objects into an existing database.

Here is an example of an Address entity that persist its data into a primary table (t_address) as well as two secondary tables (t_city and t_country). 
The primary table can be customized with the Table annotation and the secondary with SecondaryTable or SecondaryTables (with an 's') if more than one. 

Then it's just a matter of explicitly using the Column annotation on the attributes you want to persist in the secondary tables
 (@Column(table=“t_city”) would tell JPA to persist this particular attribute into the t_city table). 

By default the attributes are persisted in the primary.

```java
@Entity
@Table(name = "t_address")
@SecondaryTables({
    @SecondaryTable(name="t_city"),
    @SecondaryTable(name="t_country")
})
public class FirstTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String street1;
    private String street2;
    
    @Column(table="t_city")
    private String city;
    @Column(table="t_city")
    private String state;
    @Column(table="t_city")
    private String zipcode;
    
    @Column(table="t_country")
    private String country;
}

```

As a result of that, you will get three tables with the following columns :

* t_address : id, street1, street2
* t_city : id, city, state, zipcode
* t_country : id, country

Notice that the id is replicated in each table.

