package io.costax.caches.boundary;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface Cache2ndEvict {

    @Nonbinding boolean allEntities() default false;

    @Nonbinding boolean beforeInvocation() default false;

    @Nonbinding Class<?>[] entities () default {};
}
