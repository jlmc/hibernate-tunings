# Enable Entity Listener for all entities


In most cases, we register an `EntityListener` for an entity using the `@EntityListeners` annotations on an entity class. That works fine if you use different listeners for each entity class.

But it’s inconvenient if you want to assign the same listener to all entity classes. 
For that situations we can use an XML configuration to register the Entity Listener. That Listener will works for all entities.


1. The easiest way to do that is to create a file called `orm.xml` and place it in the `META-INF` folder of your jar file. All JPA implementations will read and process that file automatically.

2. The following code snippet configures the MyEntityListener class as the default EntityListener for all entity classes in the persistence unit.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd"
                 version="2.2">

    <persistence-unit-metadata>
        <persistence-unit-defaults>
            <entity-listeners>

                <entity-listener class="io.costax.hibernatetunings.listeners.AllEntitiesEntityListener">

                    <pre-persist method-name="onPrePersistHandler"/>
                    <post-load method-name="onPostLoadHandler"/>

                </entity-listener>

            </entity-listeners>
        </persistence-unit-defaults>
    </persistence-unit-metadata>

</entity-mappings>
```

3. Now we must implement the AllEntitiesEntityListener class.
Note that: we can make the event handler registration in two ways, in the xml file or using the javax.persistence callback annotation in the EntityListener. In this example we are using the xml configuration.

```java
public class AllEntitiesEntityListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllEntitiesEntityListener.class);

    //@PostLoad
    public void onPostLoadHandler(Object entityInstance) {
        log(PostLoad.class, entityInstance);
    }

    //@PrePersist
    public void onPrePersistHandler(Object entityInstance) {
        log(PreRemove.class, entityInstance);
    }

    private void log(final Class event, final Object entityInstance) {
        LOGGER.info("{} the entity [{}] with is describe by [{}]", event.getName(), entityInstance.getClass().getName(), entityInstance);
    }

}
```



