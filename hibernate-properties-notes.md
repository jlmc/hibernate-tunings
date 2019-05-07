# Hibernate important properties for performance improvement:


# disable autocommit check
```
<!-- For a RESOURCE_LOCAL configuration Hibernate 5.2.10, introduced the hibernate.connection.provider_disables_autocommit configuration property which tells
Hibernate that the underlying JDBC Connection(s) already disabled the auto-commit mode.
-->
<property name="hibernate.connection.provider_disables_autocommit" value="true"/>
```


# enable batch processing

```xml
<property name="hibernate.jdbc.batch_size" value="25"/>
<property name="hibernate.order_inserts" value="true"/>
<property name="hibernate.order_updates" value="true"/>
```

# enable statistics

```xml
<property name="hibernate.generate_statistics" value="true"/>

```

and off curse enable the loggin 

```properties
org.hibernate.stat = DEBUG
org.hibernate.engine.internal.StatisticalLoggingSessionEventListener = INFO
```

```xml
<logger level="info" name="org.hibernate.engine.internal.StatisticalLoggingSessionEventListener"/>
```



# Hibernate hbm2ddl


hibernate.hbm2ddl.auto Automatically validates or exports schema DDL to the database when the SessionFactory is created. With create-drop, the database schema will be dropped when the SessionFactory is closed explicitly.

e.g. validate | update | create | create-drop

validate: validate the schema, makes no changes to the database.
update: update the schema.
create: creates the schema, destroying previous data.
create-drop: drop the schema when the SessionFactory is closed explicitly, typically when the application is stopped.

```xml
<property name="hibernate.hbm2ddl.auto" value="auto"/>
```

# disable jpa.open-in-view in spring boor
By default Spring boot spring.jpa.open-in-view is enabled.
This property will register an OpenEntityManagerInViewInterceptor, which registers an EntityManager to the current thread, so you will have the same EntityManager until the web request is finished. It has nothing to do with a Hibernate SessionFactory.

So, make sure that in the application.properties configuration file, you have the following entry:


```properties
spring.jpa.open-in-view=false
```

# add more information to Session Metrics stats

a project example can be found in: conmection-lifecycle-resource-local-disables_autocommit-property


``` xml
<property name="hibernate.stats.factory" value="io.costax.hibernatetunning.statistics.TransactionStatisticsFactory"/>

```


# Identifier optimizer configurations

- hibernate.id.new_generator_mappings is true by default in hibernate 5,
  the JPA mapping above will use the **SequenceStyleGenerator**
  By default, the **SequenceStyleGenerator** and **TableGenerator** identifier generators uses the pooled optimizer.
  If the **hibernate.id.optimizer.pooled.prefer_lo**
  configuration property is set to **true**, Hibernate will use the pooled-lo optimizer by default.

```xml
    <property name="hibernate.id.new_generator_mappings" value="true"/>
    <property name="hibernate.id.optimizer.pooled.prefer_lo" value="false"/>
```


# set Hibernate time_zone 

- Tell Hibernate which timezone it shall use and set it to a timezone without 
daylight saving time, e.g. UTC. You can do that with the configuration parameter 
hibernate.jdbc.time_zone

```xml
    <property name="hibernate.jdbc.time_zone" value="UTC"/>
```


# hibernate.connection.release_mode

* Prior to Hibernate 5.2, the connection acquisition was controlled via the hibernate.connection.acquisition_mode configuration property while the connection release strategy used to be configured through hibernate.connection.release_mode property.
```
hibernate.connection.acquisition_mode
hibernate.connection.release_mode
```

* Since 5.2, both the connection acquisition and release behavior are defined by the hibernate.connection.handling_mode property.

```
hibernate.connection.handling_mode
```


# Reserved keywords

Because SQL is a declarative language, the keywords that form the grammar of the language are reserved for internal use, and they cannot be employed when defining a database identifier (e.g. catalog, schema, table, column name).

We can make escape from reserved words basically we have two options, **manual** or **global**:

###### Manual

```java
@Entity(name = "Table")
@javax.persistence.Table(name = "\"Table\"")
public class Table {
 
    @Id
    @GeneratedValue
    private Long id;
 
    @Column(name = "\"catalog\"")
    private String catalog;
 
    @Column(name = "\"schema\"")
    private String schema;
 
    private String name;
 
    @Column(name = "\"desc\"")
    private String description;
 
    //Getters and setters omitted for brevity
}
```

###### Global

Define the hibernate.globally_quoted_identifiers property to true in the persistence.xml configuration file:
This way, Hibernate is going to escape all database identifiers even those properties or tables that are not reserved words, meaning that we don’t need to manually escape the table or column names:

```xml
<property name="hibernate.globally_quoted_identifiers" value="true"/>
```

## Query plan cache

The query plan cache is shared by entity and native queries, and its size is controlled by the
following configuration property:

`<property name="hibernate.query.plan_cache_max_size" value="2048"/>`

By default, the QueryPlanCache stores 2048 plans which is sufficient for many small and
medium-sized enterprise applications.

For native queries, the QueryPlanCache stores also the ParameterMetadata which holds info about
parameter name, position, and associated Hibernate type.
The ParameterMetadata cache is controlled via the following configuration property:

`<property name="hibernate.query.plan_parameter_metadata_max_size" value="128"/>`

If the application executes more queries than the QueryPlanCache can hold, there is going to be
a performance penalty due to query compilation.
