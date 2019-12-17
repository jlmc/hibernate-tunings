package io.costax.jsonb;

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
        return isPublicOrProtectedGetterMethod(method);
    }

    private static boolean isPublicOrProtectedGetterMethod(Method method){
        if (!method.getName().startsWith("get")) return false;
        if (method.getParameterTypes().length != 0) return false;
        if (void.class.equals(method.getReturnType())) return false;
        if (method.isAnnotationPresent(JsonbTransient.class)) return false;
        return Modifier.isProtected(method.getModifiers()) || Modifier.isPublic(method.getModifiers());
    }

}
