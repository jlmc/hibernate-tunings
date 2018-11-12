# Performance Note Java Persistence and Hibernate

## Agenda

1 - Get-Started

    1.3 - logging-sql-statements
    1.4 - schema-management
    
2 - Connections    

    2.1 - connection-manager (only documentation)
    2.2 - connection-lifecycle
    
3 - Types
    
    3.1 - jpa-and-hibernate-types
    3.2 - custom-hibernate-types
    3.3 - open source hibernate types

4 - Identifiers

   4.1 - Identifier    
    

## Performance Facts

“More than half of application performance bottlenecks originate in the database” - http://www.appdynamics.com/database/


“Like us, our users place a lot of value in speed — that's why we've decided to take site speed into account in our search rankings.”
Google Ranking - https://webmasters.googleblog.com/2010/04/using-site-speed-in-web-search-ranking.html


“It has been reported that every 100ms of latency costs Amazon 1% of profit.”
http://radar.oreilly.com/2008/08/radar-theme-web-ops.html

## JPA vs Hibernate

* JPA is only a specification. It describes the interfaces that the client operates with and the standard object-relational mapping metadata (annotations, XML).

* Although it implements the JPA specification, Hibernate retains its native API for both backward compatibility and to accommodate non-standard features.


## Extra-tunnings
    
> A - query-pagination-using-window-functions, How to resolve the HHH000104: firstResult/maxResults specified with collection fetch; applying in memory!     

> B - Fix N + 1 probleam with  hibernate-enhance-plugin  

> C - override-generator-strategy: override the identity and sequence generation strategy
