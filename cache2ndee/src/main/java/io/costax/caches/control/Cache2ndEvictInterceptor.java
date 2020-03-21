package io.costax.caches.control;

import io.costax.caches.boundary.Cache2ndEvict;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.stream.Stream;

@Cache2ndEvict
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class Cache2ndEvictInterceptor implements Serializable {

    @Inject
    Cache2ndManager cache2ndManager;

    @AroundInvoke
    public Object audit(final InvocationContext context) throws Exception {
        /*
        final Method method = context.getMethod();
        final Object target = context.getTarget();
        final Object[] params = context.getParameters();
         */

        final Cache2ndEvict annotation = getCacheEvict(context);

        if (annotation != null && annotation.beforeInvocation()) {
            evict(annotation);
        }

        Object proceed = context.proceed();

        if (annotation != null && !annotation.beforeInvocation()) {
            evict(annotation);
        }

        return proceed;
    }

    private void evict(final Cache2ndEvict annotation) {
        if (annotation.allEntities()) {
            cache2ndManager.evictAllCache();
            return;
        }

        if (annotation.entities().length > 0) {
            Stream.of(annotation.entities())
                    .forEach(cache2ndManager::evictCacheForEntity);
        }
    }

    private Cache2ndEvict getCacheEvict(InvocationContext context) {

        final Method method = context.getMethod();
        Cache2ndEvict annotationInMethod = method.getAnnotation(Cache2ndEvict.class);

        if (annotationInMethod != null) {
            return annotationInMethod;
        }

        return method.getDeclaringClass().getAnnotation(Cache2ndEvict.class);
    }
}
