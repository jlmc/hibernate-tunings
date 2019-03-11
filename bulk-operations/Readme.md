# Bulk Operations

- Updating/deleting entities one by one
  - Feels natural in an OO language
  - Can be inefficient
- SQL supports bulk operations
  - JPA also, but only with queries
  
  
## JPQL 

- Bulk update

```
Query q = em.createQuery("UPDATE Project a SET a.title = CONCAT(a.title ,' - updated') WHERE a.id >= 3");
q.executeUpdate();
```


- Bulk delete

```
em.createQuery("DELETE Project a WHERE a.id >= 5").executeUpdate();  
```


## Criteria API

- Bulk update

```
CriteriaUpdate<Project> update = cb.createCriteriaUpdate(Project.class);
Root<Project> a = update.from(Project.class);
update.set(Project_.title, cb.concat(a.get(Project_.title), " - updated"));
```


- Bulk delete

```
CriteriaDelete<Project> delete = cb.createCriteriaDelete(Project.class);
Root<Project> e = delete.from(Project.class);
```

## Persistence Context

- Hibernate performs update/delete on the database
- PersistenceContext and 1st Level Cache are not updated
  - Don’t use entities in combination with bulk queries!
  - Detach entities before you perform the query
  
  
## Summary

- Updating/deleting entities one by one can be inefficient
- Bulk operation handle huge numbers of entities better
- JPQL and Criteria API support bulk operations
- PersistenceContext gets not updated
  - Don’t use entities and bulk operations in the same session
  - Detach entities before you perform the query