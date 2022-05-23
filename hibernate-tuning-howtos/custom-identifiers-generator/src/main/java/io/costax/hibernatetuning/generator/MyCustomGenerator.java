package io.costax.hibernatetuning.generator;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Properties;
import java.util.stream.Stream;

public class MyCustomGenerator implements IdentifierGenerator, Configurable {

    private String prefix;

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
        final String classSimpleName = obj.getClass().getSimpleName();
        final String identifierPropertyName = getIdentifierPropertyName(session, obj);
        final String query = String.format("select %s from %s", identifierPropertyName, classSimpleName);

        Stream<String> ids = session.createQuery(query, String.class).stream();

        long max = ids.map(Object::toString)
                .map(o -> o.replace(prefix + "-", ""))
                .mapToLong(Long::valueOf)
                .max()
                .orElse(0L);

        return prefix + "-" + (max + 1);
    }

    @Override
    public void configure(Type type, Properties properties, ServiceRegistry serviceRegistry) throws MappingException {
        prefix = properties.getProperty("prefix");
    }

    private String getIdentifierPropertyName(final SharedSessionContractImplementor session, final Object obj) {
        return session.getEntityPersister(obj.getClass().getName(), obj).getIdentifierPropertyName();
    }

    private String getTableName(final Object obj) {
        final Class<?> aClass = obj.getClass();
        final Table annotation = aClass.getAnnotation(Table.class);

        final String classSimpleName = obj.getClass().getSimpleName();

        if (annotation != null) {

            StringBuilder tableName = new StringBuilder();

            if (!annotation.schema().trim().isEmpty()) {
                tableName.append(annotation.schema()).append(".");
            }

            if (!annotation.name().trim().isEmpty()) {
                tableName.append(annotation.name());
            } else {
                tableName.append(classSimpleName);
            }

            return tableName.toString();
        } else {
            return classSimpleName;
        }
    }
}
