# How to Fix OneToOne N+1 with manual enhance

From a database perspective, the one-to-one association is based on a foreign key that is constrained to be unique. 
This way, a parent row can be referenced by at most one child record only.

## Problem

- We are using Hibernate higher than version 5.1.x
- We have a `@OneToOne` bidirectional relationship with the **N+1 problem**
- We can't use the documented plugin [hibernate enhance](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#BytecodeEnhancement)
    
    - Because for example we are not able to change/add more annotations to all the entities of your system
    - Running the plugin with `<enableLazyInitialization>true</enableLazyInitialization>` will change the behavior of all our mappings `@ManyToOne(fetch = FetchType.LAZY)`, to fix the new problem we have to add a new annotations in all ours the mappers `ToOne` the well-documented `@LazyToOne(LazyToOneOption.NO_PROXY)`


## Solution

In the following example, the User entity represents the parent-side, while the Details is the child-side of the one-to-one association. 

As you may already know the mapping `@OneToOne(fetch = FetchType.LAZY)` not behaving lazily, even though the configuration `fetch = FetchType.LAZY` is present. This is because Hibernate cannot know whether the other Entity is null or not without execute an extra select.


1. To fix the problem the first thing we have to do is to add the annotation `@LazyToOne` in the `user.details` attribute. 

```java
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user",
            optional = true,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Details details;

```

2. Then we have to implement the interface `org.hibernate.engine.spi.PersistentAttributeInterceptable` changing also the get and set methods of the details attribute. After this, classes will looks as below:

```java
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;

import javax.persistence.*;

@Entity
public class User implements org.hibernate.engine.spi.PersistentAttributeInterceptable {

    @Id
    private Integer id;

    @LazyToOne(LazyToOneOption.NO_PROXY)
    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "user",
            optional = true,
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Details details;

    @Transient
    private org.hibernate.engine.spi.PersistentAttributeInterceptor interceptor;

    
    public Details getDetails() {
        if (interceptor != null) {
            return (Details) interceptor.readObject(this, "details", details);
        }
        return details;
    }

    void setDetails(final Details details) {
        if (interceptor != null) {
            this.details = (Details) interceptor.writeObject(this, "details", this.details, details);
            //return;
        }
        this.details = details;
    }

    public void defineDetails(Details d) {
        if (this.getDetails() != null) {
            this.getDetails().setUser(null);
            this.details = null;
        }

        if(d != null) {
            d.setUser(this);
        }
        this.details = d;
    }

    @Override
    public PersistentAttributeInterceptor $$_hibernate_getInterceptor() {
        return interceptor;
    }

    @Override
    public void $$_hibernate_setInterceptor(final PersistentAttributeInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    // Others getters and setters, if necessary
}

```

```java
@Entity
public class Details {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nickName;

    @OneToOne @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    void setUser(final User user) {
        this.user = user;
    }

    User getUser() {
        return user;
    }

    // Others getters and setters, if necessary
}
```


That it. What we did was tell to Hibernate our class User has already been "instrumented" and we are giving the behavior we would like to have in the details attribute.


## What about for hibernate versions under 5.1.x?

-  For versions under the hibernate 5.1 we can use a similar solution.
-  The only thing that is different are the names of the hibernate interfaces.

    - The parent-side should implements the `org.hibernate.bytecode.internal.javassist.FieldHandled`
    - We will be using a `FieldHandler` instead of `PersistentAttributeInterceptor`
    
    

   