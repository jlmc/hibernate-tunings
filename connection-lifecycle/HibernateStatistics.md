# 2.4 Hibernate Statistics

Hibernate and JPA hide the database access logic and make it quite easy to write inefficient code even if you know the most common reasons for performance issues. As a result, a lot of performance issues appear on production systems and it is often difficult to identify the specific problem.


One of the main reasons for we do not identify problems in development time is the small size of the database, in development we normally do not have enough data to identify the problems


TIP:
- one way to identify the problem in small development DB is to use a hibernate session statistics help:
- The hibernate session knows everything that goes on. Statistics can give us very relevant data.
- By default the statistics are disabled because they are an expensive resource, and a good state it only in development.

We can active the Hibernate statistics by:

set to true the fowling properties in persistence.xml.

```xml
<property name="hibernate.generate_statistics" value="true"/>
```

and enable the logging:

```
org.hibernate.stat = DEBUG
```


After that the Hibernate will show in the application log the statistics after each execution:

E.G.

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



