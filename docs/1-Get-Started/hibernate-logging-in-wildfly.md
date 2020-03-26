# How to enable all this logging in wildfly

In the standalone we should define the next level levels

```xml
 <subsystem xmlns="urn:jboss:domain:logging:3.0">
            <console-handler name="CONSOLE">
                <level name="INFO"/>
                <formatter>
                    <named-formatter name="COLOR-PATTERN"/>
                </formatter>
            </console-handler>
            <periodic-rotating-file-handler name="FILE" autoflush="true">
                <formatter>
                    <named-formatter name="PATTERN"/>
                </formatter>
                <file relative-to="jboss.server.log.dir" path="server.log"/>
                <suffix value=".yyyy-MM-dd"/>
                <append value="true"/>
            </periodic-rotating-file-handler>
            
            <logger category="com.arjuna">
                <level name="WARN"/>
            </logger>
            
            <logger category="org.jboss.as.config">
                <level name="DEBUG"/>
            </logger>
            
            <logger category="sun.rmi">
                <level name="WARN"/>
            </logger>
            
            <logger category="org.hibernate.stat" use-parent-handlers="true">
                <level name="DEBUG"/>
            </logger>
            <logger category="org.hibernate.type.descriptor.sql" use-parent-handlers="true">
                <level name="TRACE"/>
            </logger>
            <logger category="org.hibernate" use-parent-handlers="true">
                <level name="INFO"/>
            </logger>
            <logger category="org.hibernate.SQL" use-parent-handlers="true">
                <level name="DEBUG"/>
            </logger>
            <logger category="org.keycloak.adapters">
                <level name="OFF"/>
            </logger>
            
            
            <root-logger>
                <level name="INFO"/>
                <handlers>
                    <handler name="CONSOLE"/>
                    <handler name="FILE"/>
                </handlers>
            </root-logger>
            <formatter name="PATTERN">
                <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n"/>
            </formatter>
            <formatter name="COLOR-PATTERN">
                <pattern-formatter pattern="%K{level}%d{HH:mm:ss,SSS} %-5p [%c] (%t) %s%e%n"/>
            </formatter>
        </subsystem>
```


















