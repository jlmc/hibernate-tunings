# Difference between @NotNull and @Column(nullable = false)


Before we start it is important to note that if we want to use bean validation we should add some implementation to our project.

When your application runs in a Java EE container such as JBoss AS, one implementation is already provided by the container.

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>6.0.17.Final</version>
</dependency>
```
```xml
<dependency>
    <groupId>org.glassfish</groupId>
    <artifactId>jakarta.el</artifactId>
    <version>3.0.1-b09</version>
</dependency>
```


### Consider the the bellow example entity:

```java
@Entity
@Table
public class Video {

   @Id
    private Integer id;

   @Column(nullable = false)
   private String name;

   @NotNull
   private String description;
}
```


We are going to annalise the differences between `@Column(nullable = false)` and `@NotNull`

### The Main differences:

   - Defining specification
   - System that performs the check
   - When the check get performed  


If we let Hibernate generate the database schema, which we should not do for a production system, there is no differences between the two columns.

```sql
    create table Video (
       id integer not null,
        description varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    )
```


##### 1. Defining specification

- Are defined in two different specifications.
- the `@Column(nullable = false)` is defined by the JPA (Java Persistence API)
- the `@NotNull` id defined by the BeanValidation specification.
- Unless we running our examples under JavaEE application server, to use BeanValidation we need to add a dependency to the project.

##### 2. System that performs the check

- The NotNull annotation triggers a validation by the BeanValidation implementation **when a pre-update or a pre-persist lifecycle event gets triggered**.
This means that the validations happens within our java application.


- Hibernate does not perform any validation if we annotate an attribute with a `@Column(nullable = false)`. 
- The annotation `@Column(nullable = false)` only adds a not-null constraint to the database column if hibernate create the database table information.
- **The database then checks the constraint when we insert or update a record**.
This means that the validations happens within the Database system.



##### 3. When the check get performed 

- @NotNull

  - During **Pre-Persist** and **Pre-Update**
  - If the validation fails the Hibernate will not execute any SQL statement.
 
- @Column(nullable = false)
  
  - Only adds a not null constrain to the table definition.
  - Hibernate or any other framework will not perform any validation on the entity attributes. 
  - Hibernate just execute the SQL Insert or Update and the database will validate the constraint
  - No validation in persistence layer
  - SQL statement might fail


## Which to choose

- Always use the Bean Validation annotation 

  - Hibernate triggers the validation
  
- Always add additional DB constraint

  - Database constraint are an import tool to ensure data consistency, but they don't replace the validation in our business code.

---

# Persistence XML Bean Validation Properties

There are two ways to configure validation modes in JPA. The simplest way is to add a validation-mode element to the persistence.xml with the wanted validation mode as shown in the following example:

```xml
<persistence-unit name="auto-validation">
    ... 
    <!-- Validation modes: AUTO, CALLBACK, NONE -->
    <validation-mode>AUTO</validation-mode>
    ...
</persistence-unit> 
```

The other way is to configure the validation mode programmatically by specifying the jakarta.persistence.validation.mode property with value auto, callback, or none when creating a new JPA entity manager factory as shown in the following example:

```java
Map<String, String> props = new HashMap<String, String>();
props.put("jakarta.persistence.validation.mode", "callback");
EntityManagerFactory emf = Persistence.createEntityManagerFactory("validation", props);
```
Bean validation within JPA occurs during JPA life cycle event processing. If enabled, validation occurs at the final stage of the PrePersist, PreUpdate, and PreRemove life cycle events. Validation occurs only after all user-defined life cycle events, since some of those events can modify the entity that is being validated. By default, JPA enables validation for the default validation group for PrePersist and PreUpdate life cycle events. If you must validate other validation groups or enable validation for the PreRemove event, you can specify the validation groups to validate each life cycle event in the persistence.xml as shown in the following example:

```
<persistence-unit name="non-default-validation-groups">
    <class>my.Entity</class>
    <validation-mode>CALLBACK</validation-mode>
    
    <properties>
            <property name="jakarta.persistence.validation.group.pre-persist" value="org.apache.openjpa.example.gallery.constraint.SequencedImageGroup"/>
            <property name="jakarta.persistence.validation.group.pre-update" value="org.apache.openjpa.example.gallery.constraint.SequencedImageGroup"/>
        <property name="jakarta.persistence.validation.group.pre-remove" value="jakarta.validation.groups.Default"/>
    </property>
</persistence-unit>
```


##### validation-mode

`jakarta.persistence.validation.mode`

corresponds to the validation-mode element. Use it if you wish to use the non standard DDL value.


By default, Bean Validation (and Hibernate Validator) is activated. When an entity is created, updated (and optionally deleted), it is validated before being sent to the database. The database schema generated by Hibernate also reflects the constraints declared on the entity.
You can fine-tune that if needed:


- AUTO: if Bean Validation is present in the classpath, CALLBACK and DDL are activated. This is the default behavior.

- CALLBACK: entities are validated on creation, update and deletion. If no Bean Validation provider is present, an exception is raised at initialization time.

- DDL: (not standard, see below) database schemas are entities are validated on creation, update and deletion. If no Bean Validation provider is present, an exception is raised at initialization time.

- NONE: Bean Validation is not used at all

Unfortunately, DDL is not standard mode (though extremely useful) and you will not be able to put it in <validation-mode>. To use it, add a regular property

```xml
<property name="jakarta.persistence.validation.mode">
  ddl
</property>
```

With this approach, you can mix ddl and callback modes:

```xml
<property name="jakarta.persistence.validation.mode">
  ddl, callback
</property>
```

---

##### What about if we need to perform different business validations for `insert`, `update` or `delete` statements ?

The bean validation and the JPA are very well integrated, and it is very simple to set them up to work together to meet our needs.
For this use case we can create groups of validations interfaces, for example:

1. In the following code snippet, We can find the `@Min` and the `@Max` annotations to ensure that the minutes attribute contains a value between 100 and 1000.

```java
@Entity
@Table
public class Video {

   @Id
    private Integer id;

   @Column(nullable = false)
   private String name;

   @NotNull
   private String description;
    
   @Min(value = 100, groups = PublishedVideo.class)
   @Max(1000)
   private int minutes;

}
```

The `@Min` validation references the `PublishedVideo` interface as a ValidationGroup. 
This validation is now part of the `PublishedVideo` ValidationGroup and no longer part of the default group.

```java
public interface PublishedVideo { }
```

2. Configuring Different ValidationGroups for `Persist`, `Update` or `Delete`.

    - The next step is to set which ValidationGroups shall be validated before an entity gets persisted, updated, or removed. 
    - By default, your JPA implementation uses the default group before persisting and updating an entity and **doesn’t perform any validation before removing it**. We can change that in your persistence.xml file using the following parameters:
    
      - jakarta.persistence.validation.group.pre-persist
      - jakarta.persistence.validation.group.pre-update
      - jakarta.persistence.validation.group.pre-remove
      
    - In this example, we use the `jakarta.persistence.validation.group.pre-update` to tell Hibernate to validate the `io.costax.hibernatetunings.entities.PublishedVideo` ValidationGroup before an entity gets updated
    
    
```xml
<persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">

    <persistence-unit name="it" transaction-type="RESOURCE_LOCAL">
        <description>Hibernate Performance Tuning</description>
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>

        <!-- Validation modes: AUTO, CALLBACK, NONE -->
        <validation-mode>AUTO</validation-mode>

        <properties>

            <property name="jakarta.persistence.validation.group.pre-update"
                      value="io.costax.hibernatetunings.entities.PublishedVideo"/>

        </properties>
    </persistence-unit>
</persistence>
```    


###### More about Validation-groups

 
- `jakarta.persistence.validation.group.pre-persist`

Defines the group or list of groups to validate before persisting an entity. This is a comma separated fully qualified class name string (eg com.acme.groups.Common or com.acme.groups.Common, jakarta.validation.groups.Default). Defaults to the Bean Validation default group.

- `jakarta.persistence.validation.group.pre-update`
jakarta.persistence.validation.group.pre-update defines the group or list of groups to validate before updating an entity. This is a comma separated fully qualified class name string (eg com.acme.groups.Common or com.acme.groups.Common, jakarta.validation.groups.Default). Defaults to the Bean Validation default group.


- `jakarta.persistence.validation.group.pre-remove`
jakarta.persistence.validation.group.pre-remove defines the group or list of groups to validate before persisting an entity. This is a comma separated fully qualified class name string (eg com.acme.groups.Common or com.acme.groups.Common, jakarta.validation.groups.Default). Defaults to no group.



---
## References

- [The Bean Validation reference implementation](https://hibernate.org/validator/)
- [Bean Validation documentation PDF](https://docs.jboss.org/hibernate/stable/validator/reference/en-US/pdf/hibernate_validator_reference.pdf)
