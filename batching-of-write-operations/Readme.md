# Batching of write operations

Batching is not a real JPA or Hibernate feature, It is provider by the JDBC driver.

Hibernate only make it more comfortable to use because: 

- We only need to be activated in our configuration:
- We don't have to consider it when implements our application


```xml
<property name="hibernate.jdbc.batch_size" value="5"/>
```


The main goal of batching is to:

- Group identical PreparedStatements, reduce number of database roundtrips

## inserts statements

- Unless we want to execute inserts statements we do not have to worry that statements

If we need to insert in batching then we must consider to use sequences generator strategy. because other wise if we use identity all the batching we have no effect because the insert must be always executed to retrieve the primary key.
So GenerationType.IDENTITY is not supported.


## Order insert statements

The creation of different entities it also an advantage. The JDBC batching requires the execution of multiple similar statements. And the execution of different statements close the batch.

Creating different entities requires that hibernate create different PrepareStatements. and close the batch prematurely. Hibernate can avoid this by using the next configuration:

```xml
<property name="hibernate.order_inserts" value="true"/>
```

The previous configuration allows us to order the INSERT Statements.

- Hibernate will delay the INSERT Statements as long as possible ordering them in a way that similar JDBC Statements can be executed in the same group.


## Update statements

Before use batching for update statements we should check if we can do the same operations with balk operations.
We should prefer Balk operation instead of JPA statements.

If we can not use Balk Operation for execute the update then we can use Batching. The Order of the updates can be configured using the flowing:

```xml
<property name="hibernate.order_updates" value="true"/>
```

The Hibernate 4: configurations that allow us to Activate batching for versioned data.

```xml
<property name="hibernate.jdbc.batch_versioned_data" value="true"/>
```

In Hibernate 5 the same configuration is Activated by default.


## Delete statements

Hibernate do not support order of Delete statements