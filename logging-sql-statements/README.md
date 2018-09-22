# 3 - Logging SQL Statements




## Porque precisamos de Loggging 

precisamos de logging, porque Hibernate não é como JDBC no qual temos controlo absoluto dos sql stamments que são executados contra a base de dados.

JPA e HIbernate geram statements baseados em varias trasições/alterações de estado das entidade. tais como
* persist 
* merge
* remove

Se não usarmos statement logging, nunca saberemos o que o hibernate executa nas nossas costas.
Isso será muito mau, porque se não validarmos statement auto generados pelo hibernate, podemos estar envolvidos em enumeros problemas de performance.

Hibernate logging configuration properties

* hibernate.show_sql            Print SQL statement para a consola, deve ser evitado, e se estamos a usar uma framework de tratamento de log teremos que fazer loggin tambem nessas framework.
* hibernate.format_sql          Format o SQL antes de ester ser loggado ou print para a consola.
* hibernate.use_sql_comments    Adiciona comentários ao SQL auto gerado pelo hibernate


Na verdade o hibernate "Bihind the Schenes" usa o JBOSS Logging framework (semelhante ao SLF4J). que actua como ponte.
Assim podemos usar a framework de loggin que quisermos.

* log4j (2)
* logback via SLF4j
* java util Logging

Apenas temos de activar o nivel Debug
```
<logger name="org.hibernate.SQL" level="debug"/>
```

Para activar o log dos parametros dos statement:
```
<logger name="org.hibernate.type.descriptor.sql" level="trace"/>
```
 
 ## Porque usar um JDBC DataSource ou Driver Proxy é uma alternativa mais flexivel que o Hibernate logging
 
* Usar um external JDBC Statement Proxy é preferivel. 
* Os JDBC Driver e JDBC DataSource permitem que se criem proxies ao sua volta e assim podemos intercepar e logar muito mais do que apenas SQL statement e os valores dos parametros.
* JDBC Proxy pode oferecer outro tipo de funcionalidades que permitam por exemplo:
    * detectar statements que estão a ter um custo demasiado elevado.
    * validar o numero de statement que estão a ser executados. 
 
 
 
### P6Spy 

P6Spy é uma framework open-source que suporta as funcionalidades descritas no ponto anterior.

Este projecto é um pequeno exemplo de como usar P6Spy com hibernate e JPA
