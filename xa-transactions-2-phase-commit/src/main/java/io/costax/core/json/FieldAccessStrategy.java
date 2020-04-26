package io.costax.core.json;

import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FieldAccessStrategy implements PropertyVisibilityStrategy {

    @Override
    public boolean isVisible(Field field) {
        if (field.getName().startsWith("_persistence_")) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isVisible(Method method) {
        return isPublicOrProtectedMethod(method) &&
                method.getName().startsWith("get") &&
                !isTransient(method)
                ;
    }

    private boolean isTransient(final Method method) {
        return method.isAnnotationPresent(JsonbTransient.class) ||
                Modifier.isTransient(method.getModifiers());
    }

    private static boolean isPublicOrProtectedMethod(Method method){
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        return Modifier.isProtected(method.getModifiers()) ||
                Modifier.isPublic(method.getModifiers());
    }

}
