# When and why JPA entities should implement the Serializable interface?

## JPA specification

According to the **JPA specification**, an entity should implement `Serializable` only if it needs to be passed from one JVM to another or if the entity is used by a `Stateful` Session Bean which needs to be passivated by the EJB container.

>If an entity instance is to be passed by value as a detached object (e.g., through a remote interface), the entity class must implement the Serializable interface.


## Hibernate

Hibernate only requires that entity attributes are `Serializable`, but not the entity itself.

However, implementing the JPA specification, all the JPA requirements regarding `Serializable` entities apply to Hibernate as well.


## Tomcat

According to [Tomcat documentation](https://tomcat.apache.org/tomcat-7.0-doc/config/manager.html), the `HttpSession` attributes also need to be `Serializable`:

> Whenever Apache Tomcat is shut down normally and restarted, or when an application reload is triggered, the standard Manager implementation will attempt to serialize all currently active sessions to a disk file located via the pathname attribute. All such saved sessions will then be deserialized and activated (assuming they have not expired in the mean time) when the application reload is completed.  
>
> In order to successfully restore the state of session attributes, all such attributes MUST implement the java.io.Serializable interface.

So, if the entity is stored in the `HttpSession`, it should implement `Serializable`.