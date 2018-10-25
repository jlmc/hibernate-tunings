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