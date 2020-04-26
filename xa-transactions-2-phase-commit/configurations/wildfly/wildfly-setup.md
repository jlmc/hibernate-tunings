# Configure Wildfly 

Run all the commands inside the folder wildfly/bin

1. down jdbc driver
    ```shell script
    curl --location --output $(pwd)/postgresql-42.2.12.jar --url https://jdbc.postgresql.org/download/postgresql-42.2.12.jar
    ```

2. open a connect to jboss-cli 
    ```shell script
    ./jboss-cli.sh
    ```
    1. add postgres module
        ```shell script
        module add --name=org.postgresql --slot=main --resources=postgresql-42.2.12.jar --dependencies=javax.api,javax.transaction.api
        ```
    2. Add postgres JDBC driver
        ```shell script
        /subsystem=datasources/jdbc-driver=postgres:add(driver-name="postgres",driver-module-name="org.postgresql",driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)
        ```
    3. Add the datasource db1
        ```shell script
        xa-data-source add --name=db1DSPool \
          --jndi-name=java:jboss/datasources/db1DS \
          --enabled=true \
          --use-java-context=true \
          --use-ccm=true \
          --spy=true \
          --xa-datasource-properties=[{ ServerName=${env.POSTGRES_PORT_5432_TCP_ADDR:localhost}, PortNumber=${env.POSTGRES_PORT_5432_TCP_PORT:5432}, DatabaseName=${env.POSTGRES_DATABASE:db1} }] \
          --driver-name=postgres \
          --min-pool-size=${env.MIN_POOL_SIZE:1} \
          --max-pool-size=${env.MAX_POOL_SIZE:10} \
          --flush-strategy=IdleConnections \
          --user-name=${env.POSTGRES_USER:postgres} \
          --password=${env.POSTGRES_PASSWORD:postgres} \
          --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
          --validate-on-match=true \
          --background-validation=false \
          --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
        ```

    4. Add the datasource db2
        ```shell script
        xa-data-source add --name=db2DSPool \
          --jndi-name=java:jboss/datasources/db2DS \
          --enabled=true \
          --use-java-context=true \
          --use-ccm=true \
          --spy=true \
          --xa-datasource-properties=[{ ServerName=${env.POSTGRES_PORT_5432_TCP_ADDR:localhost}, PortNumber=${env.POSTGRES_PORT_5432_TCP_PORT:5433}, DatabaseName=${env.POSTGRES_DATABASE:db2} }] \
          --driver-name=postgres \
          --min-pool-size=${env.MIN_POOL_SIZE:1} \
          --max-pool-size=${env.MAX_POOL_SIZE:10} \
          --flush-strategy=IdleConnections \
          --user-name=${env.POSTGRES_USER:postgres} \
          --password=${env.POSTGRES_PASSWORD:postgres} \
          --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker \
          --validate-on-match=true \
          --background-validation=false \
          --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter
        ```

    5. Configure logger
        ```shell script
        /subsystem=logging/logger=org.hibernate:add(level=INFO)
        /subsystem=logging/logger=org.hibernate.SQL:add(level=DEBUG)
        /subsystem=logging/logger=org.hibernate.type.descriptor.sql:add(level=TRACE)
        /subsystem=logging/logger=org.hibernate.stat:add(level=DEBUG)
        /subsystem=logging/logger=org.hibernate.cache:add(level=DEBUG)
        /subsystem=logging/logger=org.hibernate.engine.transaction.internal.TransactionImpl:add(level=DEBUG)
        /subsystem=logging/logger=org.hibernate.engine.jdbc.batch:add(level=DEBUG)
        ```
    6. exit from jboss-cli: `exit`

3. remove source jdbc driver file: `rm -rf postgresql-42.2.12.jar`


### Notes:
- What causes Arjuna 1603 (Could not find new XAResource to use for recovering non-serializable XAResource)
- To get rid of the error, stop the jboss instance and remove the folder `$JBOSS/standalone/data/tx-object-store`