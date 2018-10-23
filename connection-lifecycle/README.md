# Connection life cycle


Hibernate Connection management

* Prior to Hibernate 5.2, the connection acquisition was controlled via the hibernate.connection.acquisition_mode configuration property while the connection release strategy used to be configured through hibernate.connection.release_mode property.
```
hibernate.connection.acquisition_mode
hibernate.connection.release_mode
```

* Since 5.2, both the connection acquisition and release behavior are defined by the hibernate.connection.handling_mode property.

```
hibernate.connection.handling_mode
```

# Hibernate Connection handling modes

For RESOURCE_LOCAL and JTA transactions, Hibernate defers the JDBC connection acquisition until an SQL statement needs to be executed or a JDBC connection method needs to be called, like getting the underlying auto-commit flag. When it comes to releasing the acquired connection

### For the RESOURCE_LOCAL:

In a Resource_local configuration the connection is released after each Transaction ends.

```
DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION
```

Although the RESOURCE_LOCAL Transaction tries to delay the connection acquisition, the JDBC connection is needed right away because Hibernate needs to check the auto-commit flag and that can only be done by calling the getAutocommit method on the JDBC connection object. Hibernate 5.2.10 introduced an optimization in this regard. If the underlying DataSource is configured to disable auto-commit 
upon fetching a new database connection, then Hibernate does not need to do that. To tell Hibernate to skip the auto-commit check, you need to set to true the hibernate.connection.provider_disables_autocommit configuration property.

```
hibernate.connection.provider_disables_autocommit
```

```
<property name="hibernate.connection.provider_disables_autocommit value="true"/>
```

configuration property.


### For JTA:

In a JTA configuration the connection is released after each statement execution.


```
DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT
```

For JTA, the connection is released after every statement.
This mode was introduced a very long time ago because some Java EE application servers were complaining about connection leaks when calling successive EJB methods. At the boundary between the outer and the inner EJB, those application servers were assuming that the previously acquired connection might still be in use, hence a warning message was issued.

We might want to set the release mode to after_transcation and check how it works.

For Hibernate 5.2

```
<property name="hibernate.connection.handling_mode" value="delayed_acquisition_and_release_after_transaction" />
```

For Hibernate 5.1 and older

```
<property name="hibernate.connection.release_mode" value="after_transaction" />
```


We should prefer to use after_transaction release mode if we are using JTA, and the our JTA Manager works fine with it.