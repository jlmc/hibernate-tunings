package io.costax.hibernatetunning.tasks;

import jakarta.persistence.EntityManager;
import org.hibernate.query.ResultListTransformer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DistinctDetachTodoResultTransformer implements ResultListTransformer<Todo> {

    private final EntityManager entityManager;

    public DistinctDetachTodoResultTransformer(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Todo> transformList(List<Todo> list) {
        Map<Serializable, Todo> identifiableMap = new LinkedHashMap<>(list.size());
        for (Object entityArray : list) {
            if (Object[].class.isAssignableFrom(entityArray.getClass())) {
                Todo todo = null;
                TodoComment comment = null;

                Object[] tuples = (Object[]) entityArray;
                for (Object tuple : tuples) {
                    if (tuple instanceof Todo) {
                        todo = (Todo) tuple;

                    } else if (tuple instanceof TodoComment) {
                        comment = (TodoComment) tuple;

                    } else if (tuple != null) {
                        throw new UnsupportedOperationException("Tuple " + tuple.getClass() + " is not supported!");
                    }
                }

                //Objects.requireNonNull(post);

                if (todo != null) {
                    if (!identifiableMap.containsKey(todo.getId())) {
                        detach(todo);

                        identifiableMap.put(todo.getId(), todo);
                        // this define is very important to prevent the N + 1 query problems
                        todo.setComments(new ArrayList<>());
                    }
                    if (comment != null) {
                        detach(comment);
                        todo.addComment(comment);
                    }
                }
            }
        }
        return new ArrayList<>(identifiableMap.values());
    }

    private void detach(Object entity) {
        // we must detach the entities being fetched because we are overwriting
        // the child collection and we don’t want that to be propagated as an entity state transition:

        // Otherwise when we use the result entities inside a transactions without merge the first the entities a:
        // org.hibernate.HibernateException: A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance: io.costax.hibernatetunning.tasks.Todo.comments
        // will be throw
        this.entityManager.detach(entity);
    }
}
