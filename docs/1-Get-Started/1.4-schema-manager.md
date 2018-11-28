# Schema-management

* do not run schema migrations manualy (that is against CI, CD or even DevOps)
* do not use **hibernate.hbm2ddl.auto** in production
* You need an automatic schema  migration tool like **Flyway** or **Liquibase**
