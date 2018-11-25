# Notas


---
## 1 Typical causes of performance problems

Muitos developers queixam-se da baixa performance do hibernate, 
mas se olharmos para alguns exemplos de aplicações (com hibernate)
vamos encontrar um padrão de causas comum para esse problema e como
 um pouco de preocupação extra podem ser facilmente resolvidos. algumas 
dessas causas são: 


 1. SELECTs muito pesados e lentos,
 	- Muitas vezes isso não é detacyavel em tempo de desenvolvimento.
 	- Em desenvolvimento usam-se database muito pequenas o que não causa o impacto necessário.
 	- Nessas queries podem precisar de:
 		-- de ser refactoradas, por terem uma complexidade muito grande
 		-- Muitos dados em produção 
 		-- pode faltar uma qualquer optimizaação com a database (index por exemplo)
 	- SERÁ INTERESSENTE ANALIZAR AS QUERIES EM EQUIPA	

 2. FETCH incorrecto

 3. Load a mesma informação muitiplas vezes
 	- acontece em aplicações multi user, todos eles partilham um mesmo subset de informação 
 	que muitas vezes nunca muda.
 	- Essa informação pode ser colocada em cache como forma de melhorar a performance.


 4. Delete e edit records one by one
 	- podemos usar bulk queries podem dar-nos uma performance muito mais elevada.

 5. toda a logica de aplicação a ser realizada no codigo Java.
 	- muitas vezes podes usar a data base para realizar certas operações uma vez que esta se
 	se encontra muito optimizada para realizar essas tarefas. 	


 ---
 ## 2 -  Indentify performance problems

Hibernate and JPA hide the database access logic and make it quite easy to write inefficient code even if you know the most common reasons for performance issues. As a result, a lot of performance issues appear on production systems and it is often difficult to identify the specific problem.

- Umas das razões para não detectar mos os problemas em tempo de desenvolvimento é o tamanho da DB, em desenvolvimento não temos os dados suficientes para causar o problema.


 DICA:
 - Uma forma de identicar o problema mesmo em DB pequenas de desenvolmento é utilizar a ajuda das estatisticas da sessão do hibernate:
 - A Sessão do hibernate são todo o que se passa. e as estatisticas podem dar-nos dados muito relevantes.
 - Por defeito as estatisticas estão desabilitadas, pois são um recurso dispendioso, é bom usa-lo apenas em desenvolvimento.


Para activas as estaticas:



Propriedade persistence.xml ou System property (esta ultima pode ser mais agil)

 * hibernate.generate_statistics = true

 Configure logging

 * org.hibernate.stat = DEBUG


 A Metricas de sessão podem dar nos ainda mais informação:

```
(defaulttask-2) Session Metrics{
	1191522 nanoseconds spent acquiring 4 JDBC connections;
	433875  nanoseconds spent releasing 4 JDBC connections;
	*4404058* nanoseconds spent preparing *6* JDBC statements;
	*12458725* nanoseconds spent executing *6* JDBC statements;
	0 nanoseconds spent executing 0 JDBC batches;
	0 nanoseconds spent performing 0 L2C puts;
	0 nanoseconds spent performing 0 L2C hits;
	0 nanoseconds spent performing 0 L2C misses;
	586896 nanoseconds spent executing 1 flushes (flushinga total of 2 entities and 2 collections);
	39434974 nanoseconds spent executing 1 partial-flushes(flushinga 
```


 +  Tempo gasto em todos os JDBC statements
 	- A primeiros números dizem nos os tempos gasto a perparar e a executar as queries.
 	- Se o hibernate passa por um tempo alto nos JDBC statement pode ser devido a uma ou mais queries lentas. Ou por uma numero elevado de SQL Statement. 



+  O segundo numero interessante (*6*) indica o numero de statements sql que foram executados, é um valor muito interessate, podemos certificar nos que não são executados mais queries do que estavamos à espera.
	- um numero de 10 queries não esperas em desenvolvimento podem resultar numa centena ou milhar de queries no sistema produtivo. problema n+1

+ 0 nanoseconds spent executing 0 JDBC batches;
	- O Batch esta activo por defeito embora o seu valor por defeito sejam 0 podemos tirar partido do batch, falaremos disso mais à fente. (16) Number of JDBC Batchs

+ A caches



NOTA: o exemplo deste modulo é o projecto IndentifyPermanceIssues


---

## 3 - Logging

 Tecnicas de logging podem ser uteis para detectar ineficiencias antes de serem problema em produção. Mas tambem podem injectar um overhead extra na aplicação. precisamos ter muito cuidado com o nivel de log que definimos.

 - o niveis em produção e desenvolvimento são diferentes.

 *Desenvolvimento* 

Em desenvolvimento é util recolher o maximo de informaçao possivel, só assim é possivel aplicar tecnicas de tuning atempadamente.

Em desenvolvimento é pretendido logar:

	1. Executed SQL queries
	2. Values of bind parameters
	3. Hibernate Statistics
	4. Cache usage


```
# basic log level for all messages
log4j.logger.org.hibernate=info

# SQL statement sand parameters
log4j.logger.org.hibernate.SQL=debug
log4j.logger.org.hibernate.type.descriptor.sql=trace

# Hibernate statistics
log4j.logger.org.hibernate.stat=debug

# 2nd level cache
log4j.logger.org.hibernate.cache=debug

```

NOTAS:
	- Não devemos usar show_sql no persistence.xml, porque:
		 -- apenas faz print para standart out
		 -- ignora a nossa configuração de LOG para alem de ficar mais lento
		 --as aplicações precisão ser fechadas, não queremos andar sempre a alterar esse ficheiro, ou inves devemos usar configurações externas ou variaveis de hambientes (system properties)
		 -- Use DEBUG logging for org.hibernate.SQL




*Produção*

Em produção o nivel de lOG deve ser o mais reduzido, o nivel em produção deve ser apenas para (ERROR). não se prentende gastar nem tempo nem processamento em logs desnecessarios.


```
# basic log level for all messages

log4j.logger.org.hibernate=error
```

NOTAS
	- Log error messages only
	- Log level WARN creates additional JDBC statements!
	- Use log level ERROR, not WARN



O Hibernate desde a version 4.0 usa o JBoss logging library, para integrar com diferentes tipos de Logging framworks como:

 - Jboss LogManager
 - Log4j 2
 - Log4j 1
 - Slf4j
 - JDK logging

O hibernate pode assim integrar com qualuer umas das APIs anteriores.
O hibernate possui varias categorias e levels de Log:

- org.hibernate 
	todas as mensagens
	Podemos usar para definir de forma generica todas as mensagens do hibernate.
	INFO


- org.hibernate.SQL
	SQL statements 
	DEBUG é o level para apresentar o sql


- org.hibernate.type.descriptor.sql
	bound and extracted parameters
	TRACE para apresentar o parameters


- org.hibernate.tool.hbm2ddl
	DDL SQL statements
	DEBUG


- org.hibernate.cache
	2ndlevel cache
	Mais informações sobre a cache
	DEBUG


- org.hibernate.stat
	Hibernate statistics
	DEBUG	





O exemplo da configuração é o projecto: /Training/Logging

```
22:30:30,405 INFO  [org.thoughts.on.java.model.TestLogging] - ... selectAuthors ...
22:30:30,558 DEBUG [org.hibernate.SQL] - select author0_.id as id1_0_, author0_.firstName as firstNam2_0_, author0_.lastName as lastName3_0_, author0_.version as version4_0_ from Author author0_ where author0_.id=1
22:30:30,566 TRACE [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([id1_0_] : [BIGINT]) - [1]
22:30:30,570 TRACE [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([firstNam2_0_] : [VARCHAR]) - [Joshua]
22:30:30,570 TRACE [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([lastName3_0_] : [VARCHAR]) - [Bloch]
22:30:30,571 TRACE [org.hibernate.type.descriptor.sql.BasicExtractor] - extracted value ([version4_0_] : [INTEGER]) - [0]
22:30:30,578 DEBUG [org.hibernate.stat.internal.ConcurrentStatisticsImpl] - HHH000117: HQL: SELECT a FROM Author a WHERE a.id = 1, time: 22ms, rows: 1
22:30:30,586 INFO  [org.hibernate.engine.internal.StatisticalLoggingSessionEventListener] - Session Metrics {
    12505 nanoseconds spent acquiring 1 JDBC connections;
    0 nanoseconds spent releasing 0 JDBC connections;
    124721 nanoseconds spent preparing 1 JDBC statements;
    4745752 nanoseconds spent executing 1 JDBC statements;
    0 nanoseconds spent executing 0 JDBC batches;
    0 nanoseconds spent performing 0 L2C puts;
    0 nanoseconds spent performing 0 L2C hits;
    0 nanoseconds spent performing 0 L2C misses;
    4821482 nanoseconds spent executing 1 flushes (flushing a total of 1 entities and 1 collections);
    41523 nanoseconds spent executing 1 partial-flushes (flushing a total of 0 entities and 0 collections)
}

```




# Enable all this logging in wildfly

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


















