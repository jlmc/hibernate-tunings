# Flyway Migrations

## Local Usage

- Create the DB Containers
    - Run the following from the project root directory
        ```
        # This will start the containers in the background and not terminate when you close the terminal

        docker-compose up -d
        ```
- Create a file somewhere on your filesystem named local.conf
    ```
    # The host IP is different for those on Windows and Macs so this is not in version control
    flyway.url=jdbc:postgresql://HOST:PORT/laser
    flyway.user=laseruser
    flyway.locations=filesystem:laser/schema,filesystem:laser/clean
    flyway.password=laser
    flyway.cleanDisabled=false
    flyway.placeholders.tunnig_server_user.password=tunning
    flyway.placeholders.tunnig_provider_user.password=tunning
    ```
- Run the migrations
    ```
    mvn flyway:migrate -Dflyway.configFile=/path/to/conf/local.conf
    ```
    
    
    eg:
    ```
    mvn flyway:migrate -Dflyway.configFile=/Users/costa/Documents/junk/dummies-projects/hibernate-tunings/database-migrations/local.conf
    ```
    or:
    ```
    mvn flyway:migrate -Dflyway.configFile=local.conf
    ```
    
- Cleaning down the migration
    ```
    # Using the same local.conf as for running the migrations
    mvn flyway:clean -Dflyway.configFile=/path/to/conf/local.conf
    ```

